package scalene.misc

import scalene.gfx.{Transformer2D, Color}
import scalene.traits.{Thing, Render}
import scalene.gfx

class SolidBackground(val color:Color) extends Thing with Render {
  def render() = gfx.gl.clear(color)
}
