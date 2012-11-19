package scalene.gfx

import scalene.core.traits.{Thing, Render, InternalTransform}
import scalene.core.{VBO, Resource}
import org.newdawn.slick.opengl.Texture
import java.io.File
import org.lwjgl.opengl.GL11._
import scalene.vector.{vec2, vec}
import scalene.components.{Position2D, PositionXY}

trait SpriteLike
extends Thing
with Position2D
with InternalTransform
with Render {
  def image:Image
  lazy val __transform = Transform.static(translate = position, scale = this.scale)
  def scale:vec2
  def render() {
    image.render()
  }
}

class Sprite(imageResource:Resource[_,Image], var position:vec2 = vec2.zero, val scale:vec2 = vec2.one)
extends SpriteLike {
  def this(str:String) = this(Resource(str)(Image.load))

  lazy val image = {
    val im = imageResource.is
    im
  }

}