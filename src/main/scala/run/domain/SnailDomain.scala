package run.domain

import scalene._
import components.{KeyDownEvent, KeyEventSource, KeyEventSink, Position}
import core.{Op, Resource, DrawOp}
import gfx._
import misc.SolidBackground
import traits._
import grizzled.slf4j.Logging
import vector._
import run.Run
import org.newdawn.slick.opengl.TextureImpl

class Snail extends Sprite("img/snail.png") with KeyEventSink {

}

object SnailDomain extends Domain2D(Run) with Logging { domain =>

  var zoom = 1.0
  val snail = new Snail
  val box = new Sprite("img/packtest/2.png")
  val boxes = (0 to 100) map (_ => box) toList
  val font = TTF("font/redhead.ttf", 100)
  val drawText = DrawOp {
    font.drawString("HELLO", vec(100,100), Color.red)
  }

  val bg = new SolidBackground(Color(0.5f, 0.5f, 0.5f))

  val thangs = (
//    drawText ::
//    snail ::
//    box ::
    boxes :::
    Nil
  )

  this += bg
  this ++= thangs
  this += new KeyEventSink {
    events += {
      case KeyDownEvent(code) => { Op.NOOP }
    }
  }

  //TODO: make it easy to copy() a state and change a few things
  object SnailState extends State(domain) {

//    override def render() {
//      bg.render()
//      snail.render()
////      boxes foreach { b =>
////        b.render()
////        font.drawString("HELLO", vec(100,100), Color.red)
////      }
//    }

    val view = ViewScheme.simple(bg.color, thangs)

    val eventSource = new KeyEventSource

    val sound = None
  }
}
