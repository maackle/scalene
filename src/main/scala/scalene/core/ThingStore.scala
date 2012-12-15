package scalene.core

import scalene.event.EventSink
import traits._
import scala.Some


trait ThingStore extends Update {

  def app:ScaleneApp

//  def drain[A <: Event, B >: A <: Event](source:EventSource[A])(sinks:Iterable[EventSinkSpecific[B]]) {
//    for(sink <- sinks if sink.isInstanceOf[EventSinkSpecific[B]]) {
//      source.presentTo(sink)
//    }
//  }

  def update() {}

  abstract override def __update() {

    super.__update()

    val us = everything

    us foreach {
      case t : Update => t.__update()
      case _ =>
    }

    us foreach {
      case t : Simulate => t.__simulate(1 / app.fps.toFloat)
      case _ =>
    }

  }

  protected def += (t:Hook) { assert(t!=this); __things += t }
  protected def -= (t:Hook) { assert(t!=this); __things -= t }
  protected def ++= (t:Seq[Hook]) { assert(t!=this); __things ++= t }
  protected def --= (t:Seq[Hook]) { assert(t!=this); __things --= t }

  private var __things = collection.mutable.Set[Hook]()
  def everything:Set[Hook] = __things.toSet
  def renderables = __things flatMap {
    case t:Render => Some(t)
    case _ => None
  }
}
