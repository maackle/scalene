package run

import scalene.core._
import scalene.gfx.{Sprite, Image, SpriteAnimation, Color}
import scalene.vector.vec2
import scala.Some
import scalene.event.{HandyHandlers, KeyHoldEvent, EventHandler}

object AnimationTesteroni extends {

  val windowSize = Some(600,600)
  val windowTitle = "Animation Test"

} with ScaleneSketch with HandyHandlers { game =>

  val images = List(1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8) map { i =>
    Resource("img/packtest/%s.png" format i)(Image.load)
  }

  val handler = zoomer(view, 0.99)()


  val things = List {
  //      new Sprite(images(0))
    new SpriteAnimation(vec2.zero, images, 200)
  }
  this ++= things
  val view = View2D.simple(Color.gray, things)

}
