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

class Snail extends Sprite("img/snail.png") with KeyEventSink {

}

object TestDomain extends Domain2D(Run) with Logging { domain =>

  var zoom = 1.0
  val snail = new Snail
  val font = TTF("font/redhead.ttf", 100)
  val drawText = DrawOp {
    font.drawString("HELLO", vec(100,100), Color.red)
  }

  val bg = new SolidBackground(Color(0.5f, 0.5f, 0.5f))

  val thangs = (
    drawText ::
    snail :: Nil
  )

  this += bg
  this ++= thangs
  this += new KeyEventSink {
    handler += {
      case KeyDownEvent(code) => { Op.NOOP }
    }
  }

  //TODO: make it easy to copy() a state and change a few things
  object AState extends State(domain) {
    val views = {
      new ViewSingle2D {
        val view = new View2D {
          val layers = Vector(
            new Layer2D(0)(bg :: Nil),
            new Layer2D(1)(thangs)
          )
        }
      }
    }

    val eventSource = new KeyEventSource

    val sound = None
  }
}
