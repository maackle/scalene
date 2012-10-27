package scalene.components

import scalene.common._
import scalene.input.{LWJGLKeyboard}
import scalene.core.Op
import scalene.helpers.MemBoolean
import org.lwjgl.input.{Keyboard=>Kbd}
import scalene.traits.{Thing, Update, Execute}
import collection.mutable
import grizzled.slf4j.{Logger, Logging}

object Event {
  type Id = Int
  object Null extends Event { val code = -1 }
}
trait Event {
  def code:Event.Id
}

trait KeyEvent extends Event {

}

case class KeyHoldEvent(code:Int) extends KeyEvent
case class KeyDownEvent(code:Int) extends KeyEvent
case class KeyUpEvent(code:Int) extends KeyEvent

trait EventSource extends Thing with Update with Logging { self =>
  protected def raise(ev:Event) {
    debug("raised: %s" format ev)
    eventQueue += ev
  }

  protected val eventQueue = mutable.ListBuffer[Event]()

  def presentTo(sink:EventSink) {
    for {
      ev <- eventQueue
      op <- sink.events.consume(ev)
    } {
      debug("event consumed: %s %s" format (ev, op) )
    }
  }

  def ++ (other:EventSource) = new EventSource {
    def update() {
      self.update()
      other.update()
    }
  }
}

/* TODO: make eventDomain dynamic to really limit the key checks,
 * maybe passing in a changeable list of all EventSinks,
 * and when it changes, go through and check isDefinedAt for each value
 * */
class KeyEventSource extends EventSource {
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

object EventHandler {
  type HandlerFn = PartialFunction[Event, Op]
  lazy val empty:HandlerFn = {
    case Event.Null => Op.NOOP
  }
}

trait EventHandler {
  import EventHandler._
  private var _fn:HandlerFn = EventHandler.empty
  def consume(ev:Event) = {
    _fn.lift(ev) map { op =>
      Logger("EventHandler").debug("consumed %s and got: %s" format(ev, op))
      op()
    }
  }
  def += (f:PartialFunction[Event, Op]) {
    if(_fn == EventHandler.empty) _fn = f
    else _fn = _fn orElse f
  }
}

trait EventSink extends Thing {
  val events = new EventHandler {}
//  def consume(source:EventSource)
}

trait KeyEventSink extends EventSink with LWJGLKeyboard {
//  def consume(source:EventSource) = source.presentTo(this)
}

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