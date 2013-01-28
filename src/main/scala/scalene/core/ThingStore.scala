package scalene.core

import scalene.event.EventSink
import traits._
import scala.Some
import scalene.physics.Physical
import scalene.misc.Easing

trait IndexedThingStoreSpecific[T] extends ThingStore[T] {

  def updateSelf:Boolean
  override protected val __things = collection.mutable.ListBuffer[T]()

  protected[scalene] def += (t:T) { __things += t }
  protected[scalene] def ++= (t:Seq[T]) { __things ++= t }
}

trait IndexedThingStore extends ThingStore[Any] {

  protected val __things = collection.mutable.ListBuffer[Any]()

  protected[scalene] def += (t:Any) { __things += t }
  protected[scalene] def ++= (t:Seq[Any]) { __things ++= t }
}

trait HashedThingStore extends ThingStore[Any] { self =>

  protected val __things = collection.mutable.Set[Any]()

  protected[scalene] def += (t:Any) {  __things += t }
  protected[scalene] def ++= (t:Seq[Any]) { __things ++= t }
  protected[scalene] def -= (t:Any) { __things -= t }
  protected[scalene] def --= (t:Seq[Any]) { __things --= t }

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

  abstract override def __update(dt:Float) {

    // LET THIS BE.  This way you must add the ThingStore to itself in order to update
//    super.__update(dt)

    for (t <- everything) {
      val self = this
      t match {
        case `self` => this.update(app.dt)
        case t:Update => t.__update(app.dt)
        case _ =>
      }
    }

  }

  protected def __things:Traversable[M]
  def everything:Traversable[M] = __things

  def renderables = __things flatMap {
    case t:Render => Some(t)
//    case t:Resource[Render] => Some(t.is)
    case _ => None
  }
}
