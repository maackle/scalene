package scalene.gfx

import scalene.traits.{InternalTransform, Render, Thing}
import scalene.core.{VBO, Resource}
import org.newdawn.slick.opengl.Texture
import java.io.File
import org.lwjgl.opengl.GL11._
import scalene.vector.{vec2, vec}
import scalene.components.{Position2D, PositionXY}

class Sprite(image:Resource[_,Image], var position:vec2 = vec2.zero, val scale:vec2 = vec2.one)
extends Thing
with Position2D
with InternalTransform
with Render {
  def this(str:String) = this(Resource(str)(Image.load))
  lazy val __transform = Transform.static(translate = position, scale = this.scale)

  lazy val im = {
    val im = image.is
    im
  }

  def render() {
    im.render()
  }
}