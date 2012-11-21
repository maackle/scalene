package scalene.core

import scalene.event.EventSink
import traits.{Simulate, Update, Thing}

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 11/13/12
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
trait ThingStore extends Thing {

  def app:ScaleneApp

//  def drain[A <: Event, B >: A <: Event](source:EventSource[A])(sinks:Iterable[EventSinkSpecific[B]]) {
//    for(sink <- sinks if sink.isInstanceOf[EventSinkSpecific[B]]) {
//      source.presentTo(sink)
//    }
//  }

  def update() {
    val us = updateables
    val sinks:Set[EventSink] = (updateables + this) flatMap {
      case s:EventSink => Some(s)
      case _ => None
    }

    us foreach {
      case t : Update => t.__update()
      case _ =>
    }

    us foreach {
      case t : Simulate => t.__simulate(1/app.fps.toDouble)
      case _ =>
    }

    for {
      source <- app.eventSources
      sink <- sinks
    } {
      source.presentTo(sink)
    }
  }

  protected def += (t:Thing) { assert(t!=this); __things += t }
  protected def -= (t:Thing) { assert(t!=this); __things -= t }
  protected def ++= (t:Seq[Thing]) { assert(t!=this); __things ++= t }
  protected def --= (t:Seq[Thing]) { assert(t!=this); __things --= t }

  private var __things = collection.mutable.Set[Thing]()
  def updateables:Set[Thing] = __things.toSet
}
