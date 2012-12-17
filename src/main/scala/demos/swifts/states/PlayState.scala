package demos.swifts.states

import scalene.core.{Layer2D, View2D, State}
import demos.swifts.TheSwifts
import scalene.gfx.{VectorBatch, Color}
import demos.swifts.things.{Hawk, SwiftSwarm}
import org.lwjgl.opengl.GL11
import scalene.vector.{vec2, vec}
import scalene.event.HandyHandlers
import scalene.components.CircleShape

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 15/12/12
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
class PlayState extends State(TheSwifts) with HandyHandlers {

  val hawk = new Hawk

  val swarm = new SwiftSwarm(1000, hawk)

  val chimney = new CircleShape {
    def position: vec2 = vec2.zero
    def radius = 20
    override def render() {
      Color.green.bind()
      super.render()
    }
  }

  this += swarm
  this += hawk

  val view = View2D (Color(0x444444))(
    Layer2D(1, swarm),
    Layer2D(1.1f, chimney),
//    Layer2D(1.2f, swarm.swifts),
    Layer2D(1.2f, hawk)
  )

  val handler = {
    zoomer(view, 0.99f)() ++ panner(view, 5)()
  }
}
