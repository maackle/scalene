package scalene.event

import scalene.common._
import scalene.input.LWJGLKeyboard
import scalene.core.{View, View2D, Op}
import scalene.helpers.MemBoolean
import org.lwjgl.input.{Keyboard => Kbd, Mouse}
import scalene.core.traits.{ScaleneMixin, Update}
import collection.mutable
import grizzled.slf4j.Logging
import scalene.vector.{vec, vec2}
import scalene.core.View

object Event {
  type Id = Int
  object Null extends Event { val code = -1 }
}

trait Event {
  def code:Event.Id
}

trait KeyEvent extends Event {
  def code:Int
}

case class KeyHold(code:Int) extends KeyEvent
case class KeyDown(code:Int) extends KeyEvent
case class KeyUp(code:Int) extends KeyEvent

trait EventSource[+Unused <: Event] extends Update with Logging { self =>
  protected def raise(ev:Event) {
    debug("raised: %s" format ev)
    eventQueue += ev
  }

  protected val eventQueue = mutable.ListBuffer[Event]()

  def presentTo(sink:EventSink) {
    for {
      ev <- eventQueue
      op <- sink.handler.consumeImmediately(ev)
    } {
      debug("event consumed: %s %s" format (ev, op) )
    }
  }
}

/* TODO: make eventDomain dynamic to really limit the key checks,
 * maybe passing in a changeable list of all EventSinks,
 * and when it changes, go through and check isDefinedAt for each value
 * */
class KeyEventSource extends EventSource[KeyEvent] {
  private val keyState = collection.mutable.Map[Event.Id, MemBoolean]()
  private val downKeys = collection.mutable.Set[Event.Id]()

  def update(dt:Float) {

    eventQueue.clear()

    while(Kbd.next) {
      val code = Kbd.getEventKey
      val down = Kbd.getEventKeyState
      if(down) {
        raise( KeyDown(code) )
        downKeys += code
      }
      else {
        raise( KeyUp(code) )
        downKeys -= code
      }
    }

    for(code <- downKeys) raise( KeyHold(code) )
  }
}

trait MouseEvent extends Event {
  def windowPos:vec2
}

case class MouseHold(code:Int, windowPos:vec2) extends MouseEvent
case class MouseClick(code:Int, windowPos:vec2) extends MouseEvent
case class MouseRelease(code:Int, windowPos:vec2) extends MouseEvent

//case class MouseLeftClick(override val windowPos: vec2) extends MouseClick(0, windowPos)
//case class MouseLeftRelease(override val windowPos: vec2) extends MouseRelease(0, windowPos)
//case class MouseRightClick(override val windowPos: vec2) extends MouseClick(1, windowPos)
//case class MouseRightRelease(override val windowPos: vec2) extends MouseRelease(1, windowPos)

class MouseEventSource extends EventSource[MouseEvent] {

  private val buttonState = collection.mutable.Map[Event.Id, MemBoolean]()

  def update(dt:Float) = {
    eventQueue.clear()

    while(Mouse.next) {
      val code = Mouse.getEventButton
      val down = Mouse.getEventButtonState
      val pos = vec(Mouse.getEventX, Mouse.getEventY)
      if(code >= 0) {
        val state = buttonState.getOrElseUpdate(code, MemBoolean(2))
        state << down
        if (state.xOn)
          raise(MouseClick(code, pos))
        else if (state.xOff)
          raise(MouseRelease(code, pos))
      }
    }
  }

}

trait HandyHandlers extends EventSink {


  type DPadKeys = (Int, Int, Int, Int)
  val ArrowKeys = (KEY_UP, KEY_LEFT, KEY_DOWN, KEY_RIGHT)
  val WASDKeys = (KEY_W, KEY_A, KEY_S, KEY_D)

  def zoomer(view:View2D, ratio:Real)(out:Int=KEY_MINUS, in:Int=KEY_EQUALS) = {
    val amt = if(ratio < 1) 1 / ratio else ratio
    EventHandler {
      case KeyHold(`out`)  => view.zoom /= amt
      case KeyHold(`in`)   => view.zoom *= amt
    }
  }

  def panner(view:View2D, pixels:Real)(up:Int=KEY_W, left:Int=KEY_A, down:Int=KEY_S, right:Int=KEY_D) = EventHandler {
    case KeyHold(`left`)   => view.scroll.x -= pixels
    case KeyHold(`right`)  => view.scroll.x += pixels
    case KeyHold(`down`)   => view.scroll.y -= pixels
    case KeyHold(`up`)     => view.scroll.y += pixels
  }

  def spinner(view:View2D, degrees:Real)(ccw:Int, cw:Int) = {
    val rads = deg2rad(degrees)
    EventHandler {
      case KeyHold(`cw`)   => {
        view.rotation -= rads
      }
      case KeyHold(`ccw`)  => {
        view.rotation += rads
      }
    }
  }

  def mover(v:vec2, amount:Real)(up:Int, left:Int, down:Int, right:Int) = EventHandler {
    case KeyDown(`left`)   => v.x -= amount
    case KeyDown(`right`)  => v.x += amount
    case KeyDown(`down`)   => v.y -= amount
    case KeyDown(`up`)     => v.y += amount
    case KeyUp(`left`) |
         KeyUp(`right`) =>
      v.x = 0
    case KeyUp(`down`) |
         KeyUp(`up`) =>
      v.y = 0
  }
}

object EventHandler {
  type Partial = PartialFunction[Event, Any]
  type Lifted = Function[Event, Option[Any]]

  def bindKey(key:Int)(down: =>Unit, up: =>Unit) = EventHandler {
    case KeyDown(`key`) => down
    case KeyUp(`key`) => up
  }

  def bindvec(v:vec2, amount:Real, allowDiagonals:Boolean=true)(up:Int, left:Int, down:Int, right:Int) =
    EventHandler {
        case KeyHold(`left`) =>
          v.x = -amount
          if(!allowDiagonals) v.y = 0
        case KeyHold(`right`) =>
          v.x = +amount
          if(!allowDiagonals) v.y = 0
        case KeyHold(`down`) =>
          v.y = -amount
          if(!allowDiagonals) v.x = 0
        case KeyHold(`up`) =>
          v.y = +amount
          if(!allowDiagonals) v.x = 0
        case KeyUp(`left`) |
             KeyUp(`right`) =>
          v.x = 0
          if(!allowDiagonals) v.y = 0
        case KeyUp(`down`) |
             KeyUp(`up`) =>
          v.y = 0
          if(!allowDiagonals) v.x = 0
    }
//  def bindvec(v:vec2, amount:Real, allowDiagonals:Boolean=true)(up:Int, left:Int, down:Int, right:Int) =
//    EventHandler {
//        case KeyDown(`left`) =>
//          v.x = -amount
//          if(!allowDiagonals) v.y = 0
//        case KeyDown(`right`) =>
//          v.x = +amount
//          if(!allowDiagonals) v.y = 0
//        case KeyDown(`down`) =>
//          v.y = -amount
//          if(!allowDiagonals) v.x = 0
//        case KeyDown(`up`) =>
//          v.y = +amount
//          if(!allowDiagonals) v.x = 0
//        case KeyUp(`left`) |
//             KeyUp(`right`) =>
//          v.x = 0
//          if(!allowDiagonals) v.y = 0
//        case KeyUp(`down`) |
//             KeyUp(`up`) =>
//          v.y = 0
//          if(!allowDiagonals) v.x = 0
//    }

  def apply(fn:Partial) = new EventHandler(fn)
}

class EventHandler(val lifted:EventHandler.Lifted) {

  def this(partial:EventHandler.Partial) = this(partial.lift)

  def consumeImmediately[A <: Event](ev:A) = {
    lifted(ev) map {
      case op:Op => op()
      case _ =>
    }
  }

  def ++(other:EventHandler) = new EventHandler( ev => {
    lifted(ev).orElse(other.lifted(ev))
  })

}

object EventSinkSpecific {

}

trait EventSinkSpecific[E <: Event] extends ScaleneMixin {
  def handler:EventHandler

}

trait EventSink extends EventSinkSpecific[Event] with LWJGLKeyboard {

  implicit def fn2handler(fn:EventHandler.Partial) = EventHandler(fn)
}


//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////


/* TODO:
Message-style event triggering, instead of global events that all can hear
 !() adds events to a particular object's event queue.
!!() specifies an event that should be passed on to contained Things (probably trait of EventHandlerContainer)

Try to get event bubbling to work.
*/

trait TargetedEventHandler extends Update {
  type HandlerFn = PartialFunction[Event, Op]

  def handler:HandlerFn

  def !(ev:Event) { queue += ev }

  private val queue = mutable.Queue[Event]()
}