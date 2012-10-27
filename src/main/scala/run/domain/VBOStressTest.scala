package run.domain

import scalene.traits._
import run.Run
import org.lwjgl.BufferUtils
import org.lwjgl.opengl._
import scalene.vector.vec
import java.nio.DoubleBuffer
import scalene.core.{Op, VBO, DrawOp}
import scalene.gfx.{Color, gl}
import grizzled.slf4j.Logging
import scalene.components._
import scalene.vector.mutable.vec2
import maackle.util.Random
import scalene.components.KeyHoldEvent

object VBOStressTest extends Domain(Run) { domain =>

  object Circle {
    val N = 16
    val circleVerts = for(i <- Array.range(0, N)) yield vec.polar(1, i*math.Pi*2/N )
    val vbo = VBO.create(circleVerts)
  }
  class Circle(var position:vec2) extends Thing with Update with Render with Affine2D {
    var radius = 10.0
    val rotation = 0.0
    def scale = vec(radius, radius)
    val color = Color.hsv(Random.uniform(0,1), 1, Random.uniform(0.2f, 0.6f), 0.2f)
    def render() {
      color.bind()
      Circle.vbo.draw(GL11.GL_POLYGON)
    }
    def update() {
      radius += Random.uniform(-0.08, 0.08)
      position += vec.polar(0.1, Random.radians)
    }
  }

  class CircleTest extends State(Run) with Logging with KeyEventSink {

    events += {
      case KeyHoldEvent(KEY_MINUS) => Op { view.view.zoom *= 0.99 }
      case KeyHoldEvent(KEY_EQUALS) => Op { view.view.zoom *= 1.01 }
      case KeyDownEvent(KEY_SPACE) => Op { println(view.view.zoom) }
    }

    val N = 5000
    def rc = Random.uniform(-100, 100)
    val circles = for(i <- 1 to math.sqrt(N).toInt/2; j <- 1 to math.sqrt(N).toInt/2) yield {
      new Circle(vec.polar(math.sqrt(Random.uniform(0,1))*100, Random.radians))
    }

    this ++= circles

    val view = ViewScheme.simple(Color.hsv(0.5f,0.1f,0.5f), circles)
  }

}
