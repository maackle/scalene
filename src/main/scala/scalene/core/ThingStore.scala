package scalene.core

import scalene.event.EventSink
import traits._
import scala.Some
import scalene.physics.Physical

trait IndexedThingStore[M] extends ThingStore[M] {

  protected var __things = collection.mutable.IndexedSeq[M]()

  protected def += (t:M) { assert(t!=this); __things ++ Seq(t) }
  protected def ++= (t:Seq[M]) { assert(t!=this); __things ++= t }
}

trait HashedThingStore[M] extends ThingStore[M] {

  protected var __things = collection.mutable.Set[M]()

  protected def += (t:M) { assert(t!=this); __things += t }
  protected def ++= (t:Seq[M]) { assert(t!=this); __things ++= t }
  protected def -= (t:M) { assert(t!=this); __things -= t }
  protected def --= (t:Seq[M]) { assert(t!=this); __things --= t }

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
