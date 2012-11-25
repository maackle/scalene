package scalene.gfx

import scalene.core.Resource
import scalene.vector.vec2
import scalene.core.traits.Render
import scalene.components.Position

trait SpriteBatch extends Render {

  def imageResource:Resource[Image]
  def things:Traversable[Position]
  def positions = things.map(_.position)

  def render() {

  }
}
