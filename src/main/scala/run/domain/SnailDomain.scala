package run.domain

import scalene._
import components.Position2D
import event._
import core.{Op, Resource, DrawOp}
import event.KeyDownEvent
import gfx._
import misc.SolidBackground
import traits._
import grizzled.slf4j.{Logger, Logging}
import vector._
import run.Run
import org.newdawn.slick.opengl.TextureImpl
import maackle.util.Random

import scalene.input.LWJGLKeyboard
import org.lwjgl.opengl.GL11

trait Mover
extends EventSink
with Position2D
with Update {

  protected var velocity = vec2.zero

  val handler = EventHandler {
    case event.KeyHoldEvent(LWJGLKeyboard.KEY_SPACE) =>
      Op( velocity = position.rotate(math.Pi /2 ).unit )
    case event.KeyUpEvent(LWJGLKeyboard.KEY_SPACE) =>
      Op( velocity.set(vec2.zero) )
  }

}

object Snail {
  val im = Resource("img/snail.png")(Image.load)
}
class Snail(pos:vec2) extends Sprite(Snail.im, pos, vec2.one*2) with Mover {

  def update() {
    position += velocity
  }

}

class Box(im:Resource[_,Image], position:vec2)
  extends Sprite(im, position, vec2.one * 0.1)
  with Mover {

  def update() {
    position += velocity
  }

}

object SnailDomain extends Domain2D(Run) with Logging { domain =>

  val snail = new Snail(vec2.zero)

  val boxImages = (1 to 6) map { i =>
    Resource("img/packtest/%s.png" format i)(Image.load)
//    Resource("img/packtest/2.png" )(Image.load)
  }

  val font = TTF("font/redhead.ttf", 100)
  val drawText = DrawOp {
    font.drawString("HELLO", vec(100,100), Color.red)
  }

  val bg = new SolidBackground(Color(0.5f, 0.5f, 0.5f))

  val thangs = (
    (List.range(1,200) map { _:Int => new Snail(vec.polar.random(300)) } ) :::
    Nil
  )


  //TODO: make it easy to copy() a state and change a few things
  object SnailState extends State(domain) {

    this ++= thangs

    val view = ViewScheme.simple(bg.color, thangs)

    val eventSource = new KeyEventSource

    val sound = None
  }
}
