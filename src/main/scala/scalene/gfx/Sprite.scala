package scalene.gfx

import scalene.vector._
import scalene.traits.{Render, Thing}
import scalene.core.Resource


class Sprite(path:String, center:vec2 = null) extends Thing with Render {
  val tex = Resource(path)(new Bitmap(_))
  lazy val offset = if(center==null) vec(-tex.is.width/2, -tex.is.height/2) else -center
  override def render() {
    gl.matrix {
      gl.translate(offset)
      tex.is.blit()
    }
  }
}