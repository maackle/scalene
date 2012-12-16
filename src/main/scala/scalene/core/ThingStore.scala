package scalene.core

import scalene.event.EventSink
import traits._
import scala.Some

trait IndexedThingStore[M] extends ThingStore[M] {
  protected var __things = collection.mutable.IndexedSeq[M]()
//  override def everything:IndexedSeq[M] = __things

  protected def += (t:M) { assert(t!=this); __things ++ Seq(t) }
  protected def ++= (t:Seq[M]) { assert(t!=this); __things ++= t }
}

trait HashedThingStore[M] extends ThingStore[M] {

  protected def += (t:M) { assert(t!=this); __things += t }
  protected def ++= (t:Seq[M]) { assert(t!=this); __things ++= t }
  protected def -= (t:M) { assert(t!=this); __things -= t }
  protected def --= (t:Seq[M]) { assert(t!=this); __things --= t }

  protected var __things = collection.mutable.Set[M]()
}

trait ThingStore[M] extends Update {

  def app:ScaleneApp

//  def drain[A <: Event, B >: A <: Event](source:EventSource[A])(sinks:Iterable[EventSinkSpecific[B]]) {
//    for(sink <- sinks if sink.isInstanceOf[EventSinkSpecific[B]]) {
//      source.presentTo(sink)
//    }
//  }

  abstract override def __update() {

    super.__update()

    for (t <- everything) {
      if (t.isInstanceOf[Update]) t.asInstanceOf[Update].__update()
      if (t.isInstanceOf[Simulate]) t.asInstanceOf[Simulate].__simulate(1 / app.fps.toFloat)
    }
  }

  protected def __things:Traversable[M]
  def everything:Traversable[M] = __things

  def renderables = __things flatMap {
    case t:Render => Some(t)
    case _ => None
  }
}
