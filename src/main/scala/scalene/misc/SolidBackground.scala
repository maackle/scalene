package scalene.misc

import scalene.gfx.Color
import scalene.core.traits.{Render, Component}
import scalene.gfx

case class SolidBackground(val color:Color) extends Render {
  def render() = gfx.gl.clear(color)
}
