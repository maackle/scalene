package scalene.gfx

import scalene.core.Resource
import scalene.vector.vec2
import scalene.core.traits.Render
import scalene.components.{Position2D, Position}
import collection.mutable

trait SpriteBatch extends Render {

//  def numAllocated:Int
  def imageResource:Resource[Image]
  def things:Traversable[Position2D]

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