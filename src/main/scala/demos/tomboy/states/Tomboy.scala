package demos.tomboy.states

import scalene.gfx._
import scalene.components.{Verlet2D, Acceleration2D}
import scalene.common
import scalene.vector.{vec, vec2}
import scalene.event._
import scalene.input.LWJGLKeyboard
import scalene.core.{View2D, Domain, State}
import scalene.event.KeyDownEvent
import scalene.core.traits.{Update, ScaleneMixin, Render}
import scalene.audio.SoundStore
import demos.tomboy.Tomboy

object TomboyDomain extends Domain(Tomboy) {

  lazy val game = app

  val sounds = new SoundStore()
  sounds.addSource("snd/oddbounce.ogg")

  object Arena {
    def size = game.currentWindowSize
  }

  class Arena(boys:(Boy,Boy)) extends Render {
    lazy val size = Arena.size
    var scoreL, scoreR = 0

    lazy val font = TTF("src/main/resources/font/redhead.ttf", 80)

    lazy val (boyLeft, boyRight) = boys
    lazy val (w,h) = size

    val scoreDist = 40

    object Line {
      val thickness = 4
      val color = Color(0xF8F4C7)
    }

    import Line._

    def render() {
      color.bind()
      font.drawString(boyLeft.score.toString, vec(-scoreDist, h * 0.5f), Line.color)
      font.drawString(boyRight.score.toString, vec(scoreDist, h * 0.5f), Line.color)
      draw.rect(thickness, 2*h)
    }
  }

  trait Lasery {

  }

  object Boy {
    object Side extends Enumeration {
      val Left, Right = Value
    }
    val colorLeft = Color((195/255.).toFloat,(175/255.).toFloat, (226/255.).toFloat)
    val colorRight = Color(0xe2afd5)
    val distance = 200
  }

  case class Boy(side:Boy.Side.Value, color:Color)(keyUp:Int, keyFire:Int)
  extends Render
  with Update
  with EventSink
  with LWJGLKeyboard
  with Verlet2D {

    val (w,h) = (32,32)
    var offset:vec2 = vec2.zero
    var position:vec2 = vec( if(side == Boy.Side.Left) -Boy.distance else Boy.distance, 0)
    var velocity, acceleration = vec2.zero

    var score = 0

    val gravity = vec(0, -666)
    acceleration = gravity
    val terminalSpeed = 999
    def render() {
      gl.fill(true)
      color.bind()
      draw.rect(offset + position, w, h)
    }

    def update(dt:common.Real) {
      val arenaHeight = Arena.size._2
      velocity = velocity.limit(terminalSpeed)
      if(position.y-h/2 < -arenaHeight/2) {
        velocity.y = math.abs(velocity.y)
        score += 1
        sounds("oddbounce").play(true)
      }
      if(position.y+h/2 > arenaHeight/2) {
        velocity.y = -math.abs(velocity.y)
        score -= 1
      }
    }

    val handler = EventHandler {
      case KeyDownEvent(`keyUp`) => acceleration = gravity + vec(0,999)
      case KeyUpEvent(`keyUp`) => acceleration = gravity

//      case KeyDownEvent(keyFire) => Op { println("PEW") }
    }

  }

  class PlayTomboy extends State(TomboyDomain) with HandyHandlers {

//    def simulate(dt:common.Real) = {
//      boys.foreach{ b =>
//
//      }
//    }

    import Boy._

    lazy val arenaSize = app.currentWindowSize

    val boys:List[Boy] =
      Boy(Side.Left, colorLeft)(KEY_Z, KEY_X) ::
      Boy(Side.Right, colorRight)(KEY_COMMA, KEY_SLASH) ::
      Nil

    implicit val arena = new Arena((boys(0), boys(1)))

    val drawables = arena :: boys

    this ++= boys

    val view = View2D.simple(Color(0xC7E2C3), drawables)
    //Color(0xC7E2C3)

    val handler = (
      zoomer(view, 0.99f)(KEY_MINUS, KEY_EQUALS)
        ++ panner(view, 4)(KEY_W, KEY_A, KEY_S, KEY_D)
      )
  }


}
