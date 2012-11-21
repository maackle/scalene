package run

import scalene.gfx._
import scalene.components.Acceleration2D
import scalene.common
import scalene.vector.{vec, vec2}
import org.lwjgl.opengl.GL11
import scalene.event._
import scalene.input.LWJGLKeyboard
import scalene.core.{View2D, Domain, State, Op}
import scalene.event.KeyHoldEvent
import scalene.event.KeyDownEvent
import run.Tomboy.{Boy, Arena}
import run.Tomboy.Boy.Side
import scalene.core.traits.{Thing, Simulate, Render}


object Tomboy extends Domain(run.Run) {

  val game = app

  object Arena {
    def size = game.currentWindowSize
  }

  class Arena(boys:(Boy,Boy)) extends Thing with Render {
    val size = Arena.size
    var scoreL, scoreR = 0

    lazy val font = TTF("src/main/resources/font/redhead.ttf", 80)

    lazy val (boyLeft, boyRight) = boys
    val (w,h) = size

    val scoreDist = 40

    object Line {
      val thickness = 4
      val color = Color(0xF8F4C7)
    }

    import Line._

    def render() {
      color.bind()
      font.drawString(boyLeft.score.toString, vec(-scoreDist, h * 0.9 / 2), Line.color)
      font.drawString(boyRight.score.toString, vec(scoreDist, h * 0.9 / 2), Line.color)
      draw.rect(vec(-thickness/2, -h), thickness, 2*h)
    }
  }

  trait Lasery extends Simulate {

  }

  object Boy {
    object Side extends Enumeration {
      val Left, Right = Value
    }
    val colorLeft = Color((195/255.).toFloat,(175/255.).toFloat, (226/255.).toFloat)
    val colorRight = Color(0xe2afd5)
    val distance = 200
  }

  case class Boy(side:Boy.Side.Value, color:Color)(keyUp:Int, keyFire:Int) extends Thing
  with Render
  with Lasery
  with EventSink
  with LWJGLKeyboard
  with Acceleration2D {

    val (w,h) = (32,32)
    var offset:vec2 = vec2.zero
    var position:vec2 = vec( if(side == Boy.Side.Left) -Boy.distance else Boy.distance, 0)
    var velocity, accel = vec2.zero

    var score = 0

    val gravity = vec(0, -666)
    accel = gravity
    val terminalSpeed = 999
    def render() {
      gl.fill(true)
      color.bind()
      draw.rect(offset + position - vec(w,h)/2, w, h)
    }

    def simulate(dt:common.Real) {
      val arenaHeight = Arena.size._2
      velocity = velocity.limit(terminalSpeed)
      if(position.y-h/2 < -arenaHeight/2) {
        velocity.y = math.abs(velocity.y)
        score += 1
      }
      if(position.y+h/2 > arenaHeight/2) {
        velocity.y = -math.abs(velocity.y)
        score -= 1
      }
    }

    val handler = EventHandler {
      case KeyDownEvent(`keyUp`) => accel = gravity + vec(0,999)
      case KeyUpEvent(`keyUp`) => accel = gravity

//      case KeyDownEvent(keyFire) => Op { println("PEW") }
    }

  }

  class PlayTomboy extends State(run.Run) with HandyHandlers {

    def simulate(dt:common.Real) = {
      boys.foreach{ b =>

      }
    }

    import LWJGLKeyboard._
    import Boy._

    val arenaSize = app.currentWindowSize

    val boys:List[Boy] =
      Boy(Side.Left, colorLeft)(KEY_Z, KEY_X) ::
      Boy(Side.Right, colorRight)(KEY_COMMA, KEY_SLASH) ::
      Nil

    implicit val arena = new Arena((boys(0), boys(1)))

    val drawables = arena :: boys

    this ++= boys

    val handler = (
      zoomer(0.99)(KEY_MINUS, KEY_EQUALS)
      ++ panner(4)(KEY_W, KEY_A, KEY_S, KEY_D)
    )

    val view = View2D.simple(Color(0xC7E2C3), drawables)
    //Color(0xC7E2C3)
  }


}
