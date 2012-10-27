package scalene.traits

import scalene.components.{EventSource, EventSink}
import scalene.core.ScaleneApp

trait ThingStore extends Thing {

  def app:ScaleneApp

  def update() {
    val us = updateables
    us foreach {
      case t : Update => t.update()
      case _ =>
    }
    (us + this) foreach {
      case s : EventSink => app.eventSources.foreach(_.presentTo(s))
      case _ =>
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
