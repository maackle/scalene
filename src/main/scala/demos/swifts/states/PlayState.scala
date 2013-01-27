package demos.swifts.states

import scalene.core.{Layer2D, View2D, State}
import demos.swifts.TheSwifts
import scalene.gfx.{VectorBatch, Color}
import demos.swifts.things.{Hawk, SwiftSwarm}
import org.lwjgl.opengl.GL11
import scalene.vector.{vec2, vec}
import scalene.event._
import scalene.components.CircleShape
import scalene.core.traits.{Update, Render}
import scalene.event.KeyDown

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

  val chimney = new CircleShape with Render {
    def position: vec2 = vec2.zero
    def radius = 20
    def render() {
      Color.green.bind()
      draw()
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
    zoomer(view, 0.99f)(KEY_MINUS, KEY_EQUALS) ++ panner(view, 5)(KEY_W, KEY_A, KEY_S, KEY_D) ++ EventHandler {
      case KeyDown(KEY_P) =>
        pushState(new PauseState(this))
      case MouseClick(_, pos) =>
        println(view.toWorld(pos))
//      case _ =>
//        println(view.fromWorld(vec(0,0)))
    }
  }
}
