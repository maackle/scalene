package scalene.core

import traits.{Hook, Update}

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 11/13/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class Domain(val app:ScaleneApp) extends HashedThingStore[Hook] with Update {

  def update = ()
  def currentState:State = app.currentState

  //TODO: a way to define an initialize() method that is called once onEnter for a State in this Domain
  //TODO: a way to easily associate States with their Domains, i.e. optional containers

}

trait Domain3D extends Domain

trait DomainVoid extends Domain