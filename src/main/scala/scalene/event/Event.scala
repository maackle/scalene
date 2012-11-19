package scalene.event

import scalene.common._
import scalene.input.{LWJGLKeyboard}
import scalene.core.Op
import scalene.helpers.MemBoolean
import org.lwjgl.input.{Keyboard=>Kbd}
import scalene.core.traits.{Thing, Execute, Update}
import collection.mutable
import grizzled.slf4j.{Logger, Logging}
import scalene.vector.vec2

object Event {
  type Id = Int
  object Null extends Event { val code = -1 }
}

trait Event {
  def code:Event.Id
}

trait KeyEvent extends Event

case class KeyHoldEvent(code:Int) extends KeyEvent
case class KeyDownEvent(code:Int) extends KeyEvent
case class KeyUpEvent(code:Int) extends KeyEvent

trait MouseEvent extends Event {
  def position:vec2
}

case class MouseDownEvent(code:Int, position:vec2) extends MouseEvent
case class MouseUpEvent(code:Int, position:vec2) extends MouseEvent
case class MouseHoldEvent(code:Int, position:vec2) extends MouseEvent

trait EventSource[+Unused <: Event] extends Thing with Update with Logging { self =>
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

  def update() {

    eventQueue.clear()

    while(Kbd.next) {
      val code = Kbd.getEventKey
      val down = Kbd.getEventKeyState
      if(down) {
        raise( KeyDownEvent(code) )
        downKeys += code
      }
      else {
        raise( KeyUpEvent(code) )
        downKeys -= code
      }
    }

    for(code <- downKeys) raise( KeyHoldEvent(code) )
  }
}

class MouseEventSource extends EventSource[MouseEvent] {

  def update() = ???

}

object EventHandler {
  type HandlerFn = PartialFunction[Event, Any]

  def apply(fn:PartialFunction[Event, Any]) = new EventHandler(fn)
}

class EventHandler(val fn:EventHandler.HandlerFn) {

  def consumeImmediately[A <: Event](ev:A) = {
    fn.lift(ev) map {
      case op:Op => op()
      case _ =>
    }
  }

}

object EventSinkSpecific {

}

trait EventSinkSpecific[E <: Event] extends Thing {
  import EventSinkSpecific._
  def handler:EventHandler

}

trait EventSink extends EventSinkSpecific[Event] {

  implicit def fn2handler(fn:EventHandler.HandlerFn) = EventHandler(fn)

}

trait KeyEventSink extends EventSinkSpecific[KeyEvent] with LWJGLKeyboard {
  //  def consume(source:EventSource) = source.presentTo(this)
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