package run.domain

import run.Run
import scalene.traits.{ViewScheme, Update, Thing, State}
import scalene.components._
import scalene.core.{Op, VBO}
import scalene.vector.{vec2, vec}
import org.lwjgl.opengl.GL11
import scalene.gfx.{gl, Color}
import scalene.event.{EventHandler, KeyEventSink, KeyHoldEvent, KeyDownEvent}

class LogoTest extends State(Run) with KeyEventSink {

  val logos = for(i <- Array.range(0,1)) yield new Logo(vec(0,0))
  val view = ViewScheme.simple(Color.gray, logos)

  view.view.zoom = 5

  val handler = EventHandler {
    case KeyHoldEvent(KEY_MINUS) => Op { view.view.zoom *= 0.99 }
    case KeyHoldEvent(KEY_EQUALS) => Op { view.view.zoom *= 1.01 }
  }

  object Logo {
    import math._
    def rad(deg:Double) = math.Pi * deg / 180
    val (verts, indices) = {
      val L = 1.0
      val angle = rad(20)
      val rad60 = rad(60)
      val verts = Array(2,1,0).map(_*rad(120)+rad(-30)).flatMap { a =>
        val m = L / (2 * cos(rad(30)))
        val v = vec.polar(m, a)
        val x = L * sin(rad60-angle)/sin(rad(120))
        val d = vec.polar(x, a + rad(30) - angle)
        Seq(v, v - d)
      }
      val indices = Array(
        0,2,
        2,4,
        4,0,
        0,1,
        1,2,
        2,3,
        3,4,
        4,5,
        5,0
      )
      verts foreach( println(_) )
      (verts, indices)
    }
    val vbo = VBO.create(verts, indices=indices)
  }
  class Logo(var position:vec2) extends Thing with Update with Affine2D {
    var rotation = 0.0
    var scale = vec(10,10)

    def update() {}

    def render() {
      GL11.glPointSize(2f)
      Logo.vbo.draw(GL11.GL_LINES)
    }

  }

}
