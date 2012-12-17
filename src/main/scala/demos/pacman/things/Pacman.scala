package demos.pacman.things

import scalene.core.traits.{Update, Render}
import scalene.event.{KeyDownEvent, EventHandler, EventSink}
import scalene.components.Velocity2D
import scalene.vector.{vec, vec2}
import scalene.gfx.{Color, gl, AutoTransformer2D, draw}
import org.lwjgl.opengl.GL11
import scalene.common
import common._

class Pacman(val position:vec2)
  extends Render
  with EventSink
  with AutoTransformer2D
  with Update
  with Velocity2D {

//  val translate = position
  var rotation = 0.0
  val scale = vec2.one

  def radius = 16

  val velocity:vec2 = vec(0,0)

  val handler = EventHandler.bindvec(velocity, 100, false)(KEY_UP, KEY_LEFT, KEY_DOWN, KEY_RIGHT)

  def update() {
    if(velocity.nonZero) rotation = velocity.angle
  }

  def simulate(dt:Float) = ()

  def render() {
    val N = 16
    val x = 2
    val vertices = Array.range(x, N +1 - x).map { i =>
      vec.polar(radius, i * math.Pi*2 / N)
    }
    Color.yellow.bind()
    gl.begin(GL11.GL_POLYGON) {
      gl.vertex(0,0)
      vertices.foreach(v => gl.vertex(v))
    }
  }

}
