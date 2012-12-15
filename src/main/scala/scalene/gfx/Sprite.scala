package scalene.gfx

import scalene.core.traits.{ScaleneMixin, Render}
import scalene.core.Resource
import scalene.common._
import scalene.vector.{vec2, vec}
import scalene.components.Position2D
import scalene.common
import common._

trait SpriteLike
extends Position2D
with AutoTransformer2D
with Render {
  def image:Image
  protected def imageOffset:vec2
  def scale:vec2
  def rotation:Radian
//  def translate = position
  __transform = __transform & Transform {
    gl.translate(-imageOffset)
  }
  def render() {
    image.render()
  }
}

trait Spritely extends SpriteLike {

  def imageResource:Resource[Image]
  def imageCenter:vec2 = null
  var scale:vec2 = vec2.one

  lazy protected val imageOffset =
    if(imageCenter==null) vec(image.width/2, image.height/2) else imageCenter
  def image = {
    val im = imageResource.is
    im
  }
}

class Sprite(
              imageResource:Resource[Image],
              var position:vec2,
              var scale:vec2 = vec2.one,
              var rotation:Radian = 0,
              imageCenter:vec2 = null
              )
extends SpriteLike {

  def this(str:String, position:vec2) = this(Resource(str)(Image.load), position)

  lazy protected val imageOffset =
    if(imageCenter==null) vec(image.width/2, image.height/2) else imageCenter

  lazy val image = {
    val im = imageResource.is
    im
  }

}