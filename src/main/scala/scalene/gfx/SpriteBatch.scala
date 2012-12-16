package scalene.gfx

import scalene.core._
import scalene.core.traits.{Update, Render}
import scalene.components.{Rotation, Position2D}
import collection.mutable
import scalene.vector.vec2
import scalene.common._
import org.lwjgl.opengl.GL11

object RenderBatch {
  type MM = Position2D with Rotation
}
trait RenderBatch extends Render with Update with IndexedThingStore[Position2D with Rotation] {

  def N:Int
  def drawMode:Int

//  protected var positions:Array[vec2] = null
//  protected var rotations:Array[Radian] = null
  def vbo:VBO

  def update()

  def render() {
    vbo.draw(drawMode)
  }

}

trait VectorBatch extends RenderBatch {

  def shape:Array[vec2]
  lazy val vbo = VBO.create(N, false, false)

  def update() {
    val a = for (t <- everything; s <- shape) yield {
      (t.position + s.rotate(t.rotation))
    }
    vbo.updateVertices(a.toArray)
  }
}

trait TriangleBatch extends VectorBatch {
  val drawMode = GL11.GL_TRIANGLES
}

trait SpriteBatch extends RenderBatch {

//  def numAllocated:Int
  def imageResource:Resource[Image]

  // TODO: use single VBO for entire batch
  override def render() {
    val image = imageResource.is
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