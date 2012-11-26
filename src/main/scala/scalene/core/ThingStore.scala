package scalene.core

import scalene.event.EventSink
import traits.{Render, Simulate, Update, Component}


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
      case t : Simulate => t.__simulate(1/app.fps.toDouble)
      case _ =>
    }

  }

  protected def += (t:Component) { assert(t!=this); __things += t }
  protected def -= (t:Component) { assert(t!=this); __things -= t }
  protected def ++= (t:Seq[Component]) { assert(t!=this); __things ++= t }
  protected def --= (t:Seq[Component]) { assert(t!=this); __things --= t }

  private var __things = collection.mutable.Set[Component]()
  def everything:Set[Component] = __things.toSet
  def renderables = __things flatMap {
    case t:Render => Some(t)
    case _ => None
  }
}
