package scalene.traits

import scalene.core.ScaleneApp
import scalene.event.{EventSink, Event, EventSource, EventSinkSpecific}

trait ThingStore extends Thing {

  def app:ScaleneApp

//  def drain[A <: Event, B >: A <: Event](source:EventSource[A])(sinks:Iterable[EventSinkSpecific[B]]) {
//    for(sink <- sinks if sink.isInstanceOf[EventSinkSpecific[B]]) {
//      source.presentTo(sink)
//    }
//  }

  def update() {
    val us = updateables
    val sinks:Set[EventSink] = (updateables+this) flatMap {
      case s:EventSink => Some(s)
      case _ => None
    }
    us foreach {
      case t : Update =>
        t.update()
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

abstract class Domain(val app:ScaleneApp) extends ThingStore with Update {

  def currentState:State = app.currentState

}

abstract class Domain2D(app:ScaleneApp) extends Domain(app) {

}

trait Domain3D extends Domain
trait DomainVoid extends Domain // not sure if I'll actually use this... 0 dimensional space :)
