package scalene

import net.java.games.input.{Controller, ControllerEnvironment, Component}
import Component.Identifier
import Component.Identifier.{Axis, Button}
import maackle.util._
import collection.mutable.ListBuffer
import scalene.vector.{vec2, vec}
import grizzled.slf4j.Logging


object Controllers extends Logging {
  private var ctlEnv:ControllerEnvironment = null

  private var useControllers = false
  def enable() {
    useControllers = true
    ctlEnv = ControllerEnvironment.getDefaultEnvironment
  }
  lazy val gamepads:Array[Controller] = {
    val ctls = {
      if(useControllers)
        for(c <- ctlEnv.getControllers if c.getType == Controller.Type.GAMEPAD) yield {c}
      else
        new Array[Controller](0)
    }
    info("found %d game pad(s)" format ctls.size)
    ctls
  }
  def mice(reqd:Seq[String]=List("x", "y", "Left", "Right")):List[Controller] = {
    val ctls:List[Controller] = {
      if(useControllers)
        for(ctl:Controller <- ctlEnv.getControllers.toList if ctl.getType == Controller.Type.MOUSE) yield {
          var required = collection.mutable.Map[String,Boolean]()
          for(r <- reqd) required += r -> false
          for(cmp <- ctl.getComponents) {
            if(required.contains(cmp.getName)) required(cmp.getName) = true
          }
          if(required.filter(_._2 == false).isEmpty) ctl
          else null
        }
      else
        List[Controller]()
    }
    val usable = ctls.filter(_ != null).filter(_.getName != "Trackpad")
    println("found %d mouse(s)".format(usable.size))
    usable
  }
  lazy val all = for(c <- ctlEnv.getControllers) yield c
}

object In {
  var all = ListBuffer[In]()
}
abstract class In {
  def update()
  In.all += this
}

abstract class Gamepad extends In {
  val ctl:Controller
  override def toString = ctl.toString
}

object X360 {
  import Component.Identifier.{Button=>btn}
  val A = btn._0
  val B = btn._1
  val X = btn._2
  val Y = btn._3
  val L = btn._4
  val R = btn._5
  val stickLeft = btn._6
  val stickRight = btn._7
  val START = btn._8
  val SELECT = btn._9
  val XBOX = btn._10
  val UP = btn._11
  val DOWN = btn._12
  val LEFT = btn._13
  val RIGHT = btn._14
}
case class X360(ctl:Controller) extends Gamepad {

  println("found %d rumbler(s)".format(ctl.getRumblers.size))

  val deadzone = 0.05f
  val btns = new collection.mutable.ArrayBuffer[Boolean](15)
  val btns0 = new collection.mutable.ArrayBuffer[Boolean](15)
  for(_ <- 0 to 14) { btns.append(false); btns0.append(false) }
  val stick = vec2.zero
  val stickRight = vec2.zero
  var triggerLeft:Float = 0f
  var triggerRight:Float = 0f

  def update() {
    import Identifier.{Axis, Button}
    ctl.poll()
    assertValid(stick) {
      for(c <- ctl.getComponents) {
        val z = c.getDeadZone
        var d = c.getPollData
        if(math.abs(d) < math.max(z, deadzone)) d = 0

        c.getIdentifier match {
          case Axis.X =>
            stick.x = d
          case Axis.Y =>
            stick.y = -d
          case Axis.Z =>
            triggerLeft = d
          case Axis.RZ =>
            triggerRight = d
          case b:Button =>
            val ix = b.getName.toInt
            btns0(ix) = btns(ix)
            btns(ix) = d == 1
          case _ =>
        }
      }
    }
  }

  def buttonDown(b:Button) = {
    val ix = b.getName.toInt
    btns(ix)
  }
  def buttonUp(b:Button) = {
    val ix = b.getName.toInt
    btns(ix)
  }
  def buttonPressed(b:Button) = {
    val ix = b.getName.toInt
    !btns0(ix) && btns(ix)
  }
  def buttonReleased(b:Button) = {
    val ix = b.getName.toInt
    btns0(ix) && !btns(ix)
  }
}


case class MouseState(val position:vec2, val down:(Boolean, Boolean, Boolean)) {
  def left = down._1
  def middle = down._2
  def right = down._3
}


class Mouze(ctl:Controller, var position:vec2) extends In {

  private var dragStartPoint:vec2 = null
  private var dragBegin = false
  private var dragging = false
  private var dragEnd = false

  def leftClick    = !prev.left  &&  leftDown
  def rightClick   = !prev.right &&  rightDown
  def leftRelease  =  prev.left  && !leftDown
  def rightRelease =  prev.right && !rightDown
  def dragFrom = if(dragging || dragEnd) dragStartPoint else null
  def onDragBegin = dragBegin
  def onDragEnd = dragEnd

  var delta = vec2.zero
  val deadzone = 0.05f
  val sensitivity = 0.01f
  //      def screenPos = world2screen(position)
  val btns = collection.mutable.ArrayBuffer[Boolean](false, false, false)
  def leftDown = btns(0)
  def middleDown = btns(1)
  def rightDown = btns(2)
  var prev:MouseState = new MouseState(vec2.zero, (false, false, false))

  def update() {
    prev = new MouseState(position, (leftDown, middleDown, rightDown))
    ctl.poll()
    for(c <- ctl.getComponents) {
      var d = c.getPollData
      if(math.abs(d) < deadzone) d = 0
      c.getIdentifier.toString match {
        case "Left" => btns(0) = if(d > 0) true else false
        case "Middle" => btns(1) = if(d > 0) true else false
        case "Right" => btns(2) = if(d > 0) true else false
        case "x" => delta.x = d * sensitivity
        case "y" => delta.y = -d * sensitivity
        case _ =>
      }
    }
    position += delta

    if(leftClick) {
      dragBegin = true
      dragging = true
      dragStartPoint = snap(position)
    }
    else {
      dragBegin = false
    }
    if(leftRelease) {
      dragEnd = true
      dragging = false
    }
    else dragEnd = false
  }

  private var snaplen = 0.5f

  def snap(p:vec2) = {
    val x = math.round(p.x / snaplen)
    val y = math.round(p.y / snaplen)
    vec(x*snaplen,y*snaplen)
  }

}
