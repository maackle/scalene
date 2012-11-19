package run.domain

import scalene._
import components.Position2D
import core.traits.Update
import event._
import core._
import event.KeyDownEvent
import gfx._
import misc.SolidBackground
import grizzled.slf4j.{Logger, Logging}
import vector._
import run.Run
import org.newdawn.slick.opengl.TextureImpl
import maackle.util.Random

import scalene.input.LWJGLKeyboard
import org.lwjgl.opengl.GL11
import scala.PartialFunction

trait Spirally
extends EventSink
with Position2D
with Update {

  protected var velocity = vec2.zero

  val handler = EventHandler {
    case event.KeyHoldEvent(LWJGLKeyboard.KEY_SPACE) =>
      Op( velocity = position.rotate(math.Pi /2 ).unit * 2 )
    case event.KeyUpEvent(LWJGLKeyboard.KEY_SPACE) =>
      Op( velocity.set(vec2.zero) )
  }

}

object Snail {
  val im = Resource("img/snail.png")(Image.load)
}
class Snail(pos:vec2) extends Sprite(Snail.im, pos, vec2.one*2) with Spirally {

  def update() {
    position += velocity
  }
}

object SnailDomain extends Domain2D(Run) with Logging { domain =>

  val snail = new Snail(vec2.zero)

  val font = TTF("font/redhead.ttf", 100)

  val drawText = DrawOp {
    font.drawString("press space", vec(10,10), Color.white)
  }

  val bg = new SolidBackground(Color(0.5f, 0.5f, 0.5f))

  val snails = (
    (List.range(1,1000) map { _:Int => new Snail(vec.polar.random(300)) } ) :::
    Nil
  )


  //TODO: make it easy to copy() a state and change a few things
  object SnailState extends State(domain) with EventSink {

    this ++= snails

//    val view = ViewScheme.simple(bg.color, drawText :: snails)
    val view = new ViewSingle2D {

      val view = new View2D {
        zoom = 1
        val layers = Vector(
          new Layer2D(0)(List(bg)),
          new Layer2D(1)(snails),
          new Layer2D(1.1)(List(drawText))
        )

      }

    }
    val eventSource = new KeyEventSource

    val handler = EventHandler {
      case event.KeyHoldEvent(LWJGLKeyboard.KEY_EQUALS) => Op( view.view.zoom /= 0.99 )
      case event.KeyHoldEvent(LWJGLKeyboard.KEY_MINUS) => Op( view.view.zoom *= 0.99 )
    }

    val sound = None
  }
}
