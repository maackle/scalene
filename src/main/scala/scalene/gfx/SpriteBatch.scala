package scalene.gfx

import scalene.core._
import scalene.core.traits.{Update, Render}
import scalene.components.{Rotation, Position2D}
import collection.mutable
import scalene.vector.vec2
import scalene.common._
import org.lwjgl.opengl.GL11

trait RenderBatch[T] extends Render with Update with IndexedThingStore[T] {

  def vertexCapacity:Int
  def drawMode:Int

//  protected var positions:Array[vec2] = null
//  protected var rotations:Array[Radian] = null
  def vbo:VBO

  def update()

  def render() {
    vbo.draw(drawMode)
  }

}

trait VectorBatch[T <: Position2D] extends RenderBatch[T] {

  def update() {
    val a = {
        for(t <- everything) yield {
          t.position
        }
    }
    vbo.updateVertices(a.toArray)
  }
}

trait ShapeBatch extends VectorBatch[Position2D with Rotation] {

  def shape:Array[vec2]

  override def update() {
    val a = {
        for (t <- everything; s <- shape) yield {
          (t.position + s.rotate(t.rotation))
        }
    }
    vbo.updateVertices(a.toArray)
  }
}

trait TriangleBatch extends ShapeBatch {
  val drawMode = GL11.GL_TRIANGLES
}

trait SpriteBatch extends RenderBatch[Position2D with Rotation] {

//  def numAllocated:Int
  def image:Image

  // TODO: use single VBO for entire batch
  override def render() {
    everything.map { t =>
      val pos = t.position
      gl.matrix {
        gl.translate(pos)
        image.blit()
      }
    }
  }
}

//class SpriteBatchGhetto(val imageResource:Resource[Image]) extends SpriteBatch {
//
//  val things = mutable.Set()
//
//}