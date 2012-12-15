package scalene.core

import org.lwjgl.opengl.{GL13, GL15}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.BufferUtils
import scalene.vector.vec2
import java.nio.{FloatBuffer, IntBuffer, DoubleBuffer, Buffer}
import scala.Some
import scalene.common
import common._

abstract class VboBuffer[B <: Buffer](val buffer:B, val length:Int) {
  val id = GL15.glGenBuffers()
}
class VboDoubleBuffer(buffer:DoubleBuffer, len:Int) extends VboBuffer[DoubleBuffer](buffer, len) {
  GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
  GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
}
class VboFloatBuffer(buffer:FloatBuffer, len:Int) extends VboBuffer[FloatBuffer](buffer, len) {
  GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
  GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
}
class VboIntBuffer(buffer:IntBuffer, len:Int) extends VboBuffer[IntBuffer](buffer, len) {
  GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
  GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
}

trait VBO /*with Render*/ {
  def vertices:VboFloatBuffer
  def texCoords:Option[VboFloatBuffer]
  def indices:Option[VboIntBuffer]
  def dim:Int

//  def render() { draw(method) }

  def setup() {
    glBindBuffer(GL_ARRAY_BUFFER, vertices.id)
    glBufferData(GL_ARRAY_BUFFER, vertices.buffer, GL_STATIC_DRAW)

    texCoords map { texCoords =>
      glBindBuffer(GL_ARRAY_BUFFER, texCoords.id)
      glBufferData(GL_ARRAY_BUFFER, texCoords.buffer, GL_STATIC_DRAW)
    }

  }

  def update(verts:Array[vec2]) { //, texCoords:Array[vec2]=null, indices:Array[Int]=null) {

  }

  def draw(method:Int) {
    glEnableClientState(GL_VERTEX_ARRAY)
    if(texCoords.isDefined) glEnableClientState(GL_TEXTURE_COORD_ARRAY)

    glBindBuffer(GL_ARRAY_BUFFER, vertices.id)
    glVertexPointer(dim, GL_DOUBLE, 0, 0)

    texCoords map { texCoords =>
      assert(texCoords.length == vertices.length)
      glBindBuffer(GL_ARRAY_BUFFER, texCoords.id)
      GL13.glClientActiveTexture(GL13.GL_TEXTURE0)
      glTexCoordPointer(2, GL_DOUBLE, 0, 0)
    }

    indices match {
      case Some(indices) =>
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices.id)
        glDrawElements(method, indices.length, GL_UNSIGNED_INT, 0)
      case _ =>
        glDrawArrays(method, 0, vertices.length)
    }
    glBindBuffer(GL_ARRAY_BUFFER, GL_NONE)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE)
    glDisableClientState(GL_VERTEX_ARRAY)
    glDisableClientState(GL_TEXTURE_COORD_ARRAY)
  }
}

trait VBO2D extends VBO {
  val dim = 2
}

object VBO {

  type BufferT = FloatBuffer

  val N = 8

  var vbuf_id = 0
  var ixbuf_id = 0

  def create[v <: vec2](vertices:Array[v], texCoords:Array[v]=null, indices:Array[Int]=null) = {
    val n = vertices.length

    val vbuf = {
      val buf = BufferUtils.createFloatBuffer(n * 2)
      val coords:Array[Real] = vertices flatMap(v => Seq(v.x,v.y))
      buf.put(coords)
      buf.flip()
      buf
    }

    val tbuf = if(texCoords!=null) {
      assert(n == texCoords.length)
      val buf = BufferUtils.createFloatBuffer(n * 2)
      val coords:Array[Real] = texCoords flatMap(v => Seq(v.x,v.y))
      buf.put(coords)
      buf.flip()
      Some(buf)
    } else None

    val ixs = {
      if(indices==null) None
      else {
        val ixbuf = BufferUtils.createIntBuffer(indices.length)
        ixbuf.put(indices)
        ixbuf.flip()
        Some(new VboIntBuffer(ixbuf, indices.length))
      }
    }
    new VBO2D {
      val vertices = new VboFloatBuffer(vbuf, n)
      val texCoords = tbuf map { new VboFloatBuffer(_, n) }
      val indices = ixs
    }
  }
}