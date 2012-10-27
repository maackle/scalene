package scalene.core

import org.lwjgl.opengl.{GLContext, GL15, GL11}
import scalene.gfx.gl
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.BufferUtils
import scalene.vector.vec
import scalene.vector.mutable.vec2
import java.nio.{IntBuffer, DoubleBuffer, Buffer}
import scalene.traits.{Thing, Render}

object VboBuffer {

  // NO GOOD: need a good way to pass in length (for 2D it's half, 3D a third, etc)
//  def create(as:Array[Double]) = new VboDoubleBuffer({
//    val vbuf = BufferUtils.createDoubleBuffer(as.length*2)
//    vbuf.put(as)
//    vbuf.flip()
//    vbuf
//  }, as.length)
//
//  def create(as:Array[Int]) = new VboIntBuffer({
//    val vbuf = BufferUtils.createIntBuffer(as.length*2)
//    vbuf.put(as)
//    vbuf.flip()
//    vbuf
//  }, as.length)
}
abstract class VboBuffer[B <: Buffer](val buffer:B, val length:Int) {

  val id = GL15.glGenBuffers()

}
class VboDoubleBuffer(buffer:DoubleBuffer, len:Int) extends VboBuffer[DoubleBuffer](buffer, len) {
  GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
  GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
}
class VboIntBuffer(buffer:IntBuffer, len:Int) extends VboBuffer[IntBuffer](buffer, len) {
  GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
  GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
}

trait VBO extends Thing with Render {
  def vertices:VboDoubleBuffer
  def indices:Option[VboIntBuffer]
  def dim:Int

  def render() { draw(GL_LINE_LOOP) }

  def draw(method:Int) {
    glEnableClientState(GL_VERTEX_ARRAY)
    glBindBuffer(GL_ARRAY_BUFFER, vertices.id)
    glVertexPointer(dim, GL_DOUBLE, 0, 0)

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
  }
}

trait VBO2D extends VBO {
  val dim = 2
}

object VBO {

  type BufferT = DoubleBuffer

  val N = 8

  var vbuf_id = 0
  var ixbuf_id = 0

  def create(verts:Array[vec2], indices:Array[Int]=null) = {
    val n = verts.length
    val vbuf = BufferUtils.createDoubleBuffer(n * 2) // TODO: allow Float too
    val coords = verts flatMap(v => Seq(v.x,v.y))
    vbuf.put(coords)
    vbuf.flip()
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
      val vertices = new VboDoubleBuffer(vbuf, verts.length)
      val indices = ixs
    }
  }
}