package demos.swifts.states

import scalene.core.{View2D, State}
import demos.swifts.TheSwifts
import scalene.gfx.{VectorBatch, Color}
import demos.swifts.things.{SwiftSwarm, Swift}
import org.lwjgl.opengl.GL11
import scalene.vector.{vec2, vec}
import scalene.event.HandyHandlers

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 15/12/12
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
class Play extends State(TheSwifts) with HandyHandlers {

  val swarm = new SwiftSwarm(1000)

  this += swarm

  val view = View2D.simple(Color(0x7ebcbb), swarm)

  val handler = {
    zoomer(view, 0.99f)() ++ panner(view, 5)()
  }
}
