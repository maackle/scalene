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
  protected def imageOffset:vec2
  lazy val __transform = Transform.static(translate = position, scale = this.scale)
  def scale:vec2
  def render() {
    gl.matrix {
      gl.translate(-imageOffset)
      image.render()
    }
  }
}

class Sprite(
              imageResource:Resource[Image],
              var position:vec2 = vec2.zero,
              val scale:vec2 = vec2.one,
              imageCenter:vec2 = null
              )
extends SpriteLike {
  def this(str:String) = this(Resource(str)(Image.load))
  lazy protected val imageOffset =
    if(imageCenter==null) vec(image.width/2, image.height/2) else imageCenter
  lazy val image = {
    val im = imageResource.is
    im
  }

}