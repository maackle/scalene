package scalene.core

import traits.Update

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 11/13/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class Domain(theApp:ScaleneApp) extends ThingStore with Update {

  val app = theApp
  def currentState:State = app.currentState

}

trait Domain3D extends Domain

trait DomainVoid extends Domain