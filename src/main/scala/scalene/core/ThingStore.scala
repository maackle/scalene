package scalene.core

import scalene.event.EventSink
import traits._
import scala.Some
import scalene.physics.Physical
import scalene.misc.Easing

trait IndexedThingStore[M] extends ThingStore[M] {

  protected var __things = collection.mutable.IndexedSeq[M]()

  protected[scalene] def += (t:M) { assert(t!=this); __things ++ Seq(t) }
  protected[scalene] def ++= (t:Seq[M]) { assert(t!=this); __things ++= t }
}

trait HashedThingStore extends ThingStore[Any] { self =>

  protected var __things = collection.mutable.Set[Any]()

  protected[scalene] def += (t:Any) { assert(t!=this); __things += t }
  protected[scalene] def ++= (t:Seq[Any]) { assert(t!=this); __things ++= t }
  protected[scalene] def -= (t:Any) { assert(t!=this); __things -= t }
  protected[scalene] def --= (t:Seq[Any]) { assert(t!=this); __things --= t }

  object Transition {
    def create(seconds:Float, easing:Easing = null)(fn:Float=>Unit) = {
      val f = if(easing==null) fn else { t:Float => fn(easing(t)) }
      val transition = new Transition(seconds, f)
      self += transition
      transition
    }
//    def create(seconds:Float, easing:Easing) = {
//      val transition = new Transition(seconds, easing(_))
//      self += transition
//      transition
//    }
//    def create[A <: Easing.Ops[A]](easing:Easing, seconds:Float)(a:A, z:A) = {
//      new Transition(seconds, easing.interpolate(a,z)(_))
//    }
  }

  class Transition(var duration:Float, val fn:Float=>Unit) extends scalene.misc.Transition {
    val store = self
  }
}

trait ThingStore[M] extends Update {

  def app:ScaleneApp

//  def drain[A <: Event, B >: A <: Event](source:EventSource[A])(sinks:Iterable[EventSinkSpecific[B]]) {
//    for(sink <- sinks if sink.isInstanceOf[EventSinkSpecific[B]]) {
//      source.presentTo(sink)
//    }
//  }

  abstract override def __update(dt:Float) {

    super.__update(dt)

    for (t <- everything) {
      if (t.isInstanceOf[Update]) {
        t.asInstanceOf[Update].__update(app.dt)
      }
    }
  }

  protected def __things:Traversable[M]
  def everything:Traversable[M] = __things

  def renderables = __things flatMap {
    case t:Render => Some(t)
    case _ => None
  }
}
