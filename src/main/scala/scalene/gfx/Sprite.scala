package scalene.gfx

import scalene.core.traits.{Thing, Render}
import scalene.core.{VBO, Resource}
import scalene.common._
import scalene.vector.{vec2, vec}
import scalene.components.{Position2D, PositionXY}

trait SpriteLike
extends Thing
with Position2D
with InternalTransform
with Render {
  def image:Image
  protected def imageOffset:vec2
  def scale:vec2
  def rotation:Real
  def render() {
    gl.matrix {
      gl.translate(-imageOffset)
      image.render()
    }
  }
  val __transform = Transform {
    gl.translate(this.position)
    gl.scale(this.scale)
    gl.rotateRad(this.rotation)
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
              var rotation:Real = 0,
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