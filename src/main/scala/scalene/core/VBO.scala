package scalene.core

import org.lwjgl.opengl.{GL11, GL13, GL15}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.BufferUtils
import scalene.vector.vec2
import java.nio.{FloatBuffer, IntBuffer, DoubleBuffer, Buffer}
import scala.Some
import scalene.common
import common._

abstract class VboBuffer[+B <: Buffer](val length:Int) {
  val id = GL15.glGenBuffers()
}

class VboFloatBuffer(len:Int) extends VboBuffer[FloatBuffer](len) {

  val buffer:FloatBuffer = BufferUtils.createFloatBuffer(len)

  def setRaw(vs:Array[Float], mode:Int = GL15.GL_STREAM_DRAW) = {
    buffer.put(vs)
    buffer.flip()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, mode)
    this
  }

  def set(ps:Array[vec2], mode:Int = GL15.GL_STREAM_DRAW) = {
    setRaw(ps flatMap ( p => Seq(p.x, p.y)), mode)
  }
}

class VboIntBuffer(len:Int) extends VboBuffer[IntBuffer](len) {
  val buffer:IntBuffer = BufferUtils.createIntBuffer(len)

  def set(a:Array[Int], mode:Int = GL15.GL_STREAM_DRAW) = {
    buffer.put(a)
    buffer.flip()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, mode)
    this
  }
}

trait VBO {
  def vertices:VboFloatBuffer
  def texCoords:Option[VboFloatBuffer]
  def dim:Int = 2

  import VBO._

  def updateVertices(vs:Array[v]) = vertices.set(vs)

  protected def wrapDraw(fn: =>Unit) {
    glEnableClientState(GL_VERTEX_ARRAY)
    if(texCoords.isDefined) glEnableClientState(GL_TEXTURE_COORD_ARRAY)

    glBindBuffer(GL_ARRAY_BUFFER, vertices.id)
    glVertexPointer(dim, GL_FLOAT, 0, 0)

    texCoords map { texCoords =>
      glBindBuffer(GL_ARRAY_BUFFER, texCoords.id)
      GL13.glClientActiveTexture(GL13.GL_TEXTURE0)
      glTexCoordPointer(2, GL_FLOAT, 0, 0)
    }

    fn

    glBindBuffer(GL_ARRAY_BUFFER, GL_NONE)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE)
    glDisableClientState(GL_VERTEX_ARRAY)
    glDisableClientState(GL_TEXTURE_COORD_ARRAY)
  }

  def draw(method:Int) = wrapDraw {
    glDrawArrays(method, 0, vertices.length)
  }
}

trait VBO_Patchy extends VBO {
  def patchSize:Int

  def drawWithPatches(method:Int) {
    import org.lwjgl.opengl.GL40

    GL40.glPatchParameteri(GL40.GL_PATCH_VERTICES, patchSize); //TODO: only for opengl 4!
    draw(GL40.GL_PATCHES)
  }

  override def draw(method:Int = GL11.GL_POLYGON) = {
    for (i <- 0 to vertices.length) {
      glDrawArrays(method, i*patchSize, patchSize)
    }
  }

  def drawOne(which:Int, method:Int = GL11.GL_POLYGON) = wrapDraw {
    glDrawArrays(method, which, patchSize)
  }
}

trait VBO_Indexed extends VBO {
  def vertices:VboFloatBuffer
  def texCoords:Option[VboFloatBuffer]
  def indices:Option[VboIntBuffer]
  def consecutive:Int

  import VBO._

  override def draw(method:Int) = wrapDraw {
    indices match {
      case Some(indices) =>
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices.id)
        glDrawElements(method, indices.length, GL_UNSIGNED_INT, 0)
      case _ =>
        glDrawArrays(method, 0, consecutive)
    }
  }
}

object VBO {

  type v = vec2
  val dim = 2


  def create(size:Int, useTextures:Boolean, useIndices:Boolean) = {
    val n = size

    val vs = new VboFloatBuffer(n * dim)

    val ts = if (useTextures) Some(new VboFloatBuffer(n * 2)) else None

    val ixs = if (useTextures) Some(new VboIntBuffer(n)) else None

    new VBO {
      val vertices = vs
      val texCoords = ts
      val indices = ixs
    }
  }

  def create(vertices:Array[v], texCoords:Array[v]=null, indices:Array[Int]=null) = {
    val n = vertices.length
    assert(n == texCoords.length)

    val vs = new VboFloatBuffer(n * dim).set(vertices, GL_STATIC_DRAW)

    val ts = for(texCoords <- Option(texCoords)) yield {
      new VboFloatBuffer(n * 2).set(texCoords, GL_STATIC_DRAW)
    }

    val ixs = Option(indices) map { indices =>
      new VboIntBuffer(n).set(indices, GL_STATIC_DRAW)
    }

    new VBO {
      val vertices = vs
      val texCoords = ts
      val indices = ixs
    }
  }

}