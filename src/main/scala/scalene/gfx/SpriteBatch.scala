package scalene.gfx

import scalene.core.{VboIntBuffer, VboFloatBuffer, VBO, Resource}
import scalene.core.traits.Render
import scalene.components.Position2D
import collection.mutable

trait RenderBatch extends Render {

  type M = Position2D
  private var things:List[Traversable[M]]
  private val buf = new scalene.core.VboFloatBuffer()

  def +=(thing:M) = things ::= new M {
    def position = thing.position
  }


}

trait SpriteBatch extends RenderBatch {

//  def numAllocated:Int
  def imageResource:Resource[Image]

  // TODO: use single VBO for entire batch
  def render() {
    val image = imageResource.is
    things.map { t =>
      val pos = t.position
      gl.matrix {
        gl.translate(pos)
        image.blit()
      }
    }
  }
}

class SpriteBatchGhetto(val imageResource:Resource[Image]) extends SpriteBatch {
  val things = mutable.Set()

}