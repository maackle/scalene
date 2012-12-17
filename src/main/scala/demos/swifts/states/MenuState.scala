package demos.swifts.states

import demos.swifts.TheSwifts
import demos.swifts.things.{Hawk, SwiftSwarm}
import scalene.components.{Acceleration2D, Position2D, PositionXY, CircleShape}
import scalene.core._
import scalene.event.{EventHandler, KeyDownEvent, EventSink, HandyHandlers}
import scalene.gfx.{VectorBatch, Sprite, TTF, Color}
import scalene.vector.{vec, vec2}
import scalene.core.traits.{Render, Update}
import scalene.event.KeyDownEvent
import org.lwjgl.opengl.GL11
import collection.mutable.ArrayBuffer

trait Menu extends Render with Update with EventSink {

  def items:IndexedSeq[(String, ( ()=>Unit))]
  def font:TTF
  lazy val lineHeight = font.uni.getLineHeight

  var current = 0

  def render() {
    for(((label, action), i) <- items.zipWithIndex) {
      val pos = vec(10, i*lineHeight)
      font.drawString(label, pos, Color.white)
    }
  }

  val handler = EventHandler {
    case KeyDownEvent(KEY_DOWN) => current = (current + 1) % items.size
    case KeyDownEvent(KEY_UP) => current = (current - 1) % items.size
  }
}

trait Particle extends Acceleration2D {
  val color:Color
}

trait PointParticleSystem extends VectorBatch[Particle] {
  val drawMode = GL11.GL_POINTS
  val shape = None
  def pointSize = 10f

  lazy val vbo = VBO.create(vertexCapacity, useColors=true)

  var vecbuf = ArrayBuffer[vec2]()
  var colorbuf = ArrayBuffer[Color]()

  override def update() {

    vecbuf.clear()
    colorbuf.clear()

    for(p<-everything) {
      vecbuf += p.position
      colorbuf += p.color
    }
    vecbuf.toArray.foreach(println)
    vbo.updateVertices(vecbuf.toArray)
    vbo.updateColors(colorbuf.toArray)
  }

  override def render() {
    GL11.glPointSize(pointSize)
    super.render()
  }

}

class CloudSystem extends PointParticleSystem {

  val vertexCapacity = 5000
  val app = TheSwifts

  class CloudParticle(var position:vec2) extends Particle {
    val velocity = vec.polar.random(10)
    val acceleration = vec(0, 90f) + vec.polar.random(90)
    val color = Color.white.alpha(0.5f)

    def simulate(dt:Float) {
      color.a -= 0.0001f
      color.r -= 0.0001f
      if(color.a < 0) color.a = 0
    }

  }

  this ++= (0 until vertexCapacity) map (_ => new CloudParticle(vec(0,0)))
}

class MenuState extends State(TheSwifts) with HandyHandlers {

  val school = new Sprite(Resource.Image("img/hawks/school-low-contrast.png"), vec(0,0))
  val menu = new Menu {
    def update() {}

    def items = IndexedSeq(
      "Start" -> (()=>{ 'go }),
      "!!!!!!!!!ljk1kl1klj" -> (()=>{ 'exit })
    )

    lazy val font = TTF("font/redhead.ttf", 40)
  }

  val clouds = new CloudSystem

  this += menu
  this += clouds

  val view = View2D(
    Layer2D(school),
    Layer2D(menu),
    Layer2D(clouds)
  )

  val handler = {
    zoomer(view, 0.99f)() ++ panner(view, 5)()
  }
}
