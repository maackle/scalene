package demos.heart

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 1/26/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */

trait TimeSync {
  def state: states.Play
  def T = state.time
  lazy val tempo:Float = state.tempo
  lazy val period = 60f / tempo
}
