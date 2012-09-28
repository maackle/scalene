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
    info("raised: %s" format ev)
    eventQueue += ev
  }
  protected def eventDomain:Set[Event.Id] // so we only check events we need to
  protected val eventQueue = mutable.ListBuffer[Event]()

  def setDomain(evs:Seq[Event.Id]) { ??? }

  def presentTo(sink:EventSink) {
    for {
      ev <- eventQueue
      op <- sink.handler.consume(ev)
    } {
      info("event consumed! %s %s" format (ev, op) )
    }
  }

  def ++ (other:EventSource) = new EventSource {
    val eventDomain = self.eventDomain ++ other.eventDomain
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
  protected val eventDomain = Set(LWJGLKeyboard.keyRange:_*)
  private val keyState = collection.mutable.Map[Event.Id, MemBoolean]()

  private def updateKey(code:Event.Id) = {
    val s = keyState.getOrElseUpdate(code, MemBoolean(16))
    s << Kbd.isKeyDown(code)
    s
  }

  def update() {
    eventQueue.clear()
    for {
      code <- eventDomain
      down = updateKey(code)
    } {
      if(down.now) raise( KeyHoldEvent(code) )
      if(down.xOn) raise( KeyDownEvent(code) )
      else if(down.xOff) raise( KeyUpEvent(code) )
    }
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
      Logger("handler").info("consumed %s and got: %s" format(ev, op))

    }
  }
  def += (f:PartialFunction[Event, Op]) {
    if(_fn == EventHandler.empty) _fn = f
    else _fn = _fn orElse f
  }
}

trait EventSink extends Thing {
  val handler = new EventHandler {}
//  def consume(source:EventSource)
}

trait KeyEventSink extends EventSink {
//  def consume(source:EventSource) = source.presentTo(this)
}
