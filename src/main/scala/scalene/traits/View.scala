package scalene.traits

import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import scalene.common._
import scalene.vector._
import org.lwjgl.util.glu.GLU
import scalene.gfx
import scalene.gfx.{Transform2D, Transformer2D, Transform, gl}
import org.lwjgl.opengl.GL11

trait ViewScheme extends Render

object NoViewScheme extends ViewScheme {
  val transform = Transform.static()
  def render() {}
}

trait ViewSingle2D extends ViewSingle {
  val transform = Transform2D.identity
  override def view:View2D
}

trait ViewSingle extends ViewScheme {
  def view:View
  def render() { view.__render() }
}

trait View extends Render with InternalTransform {

  private var lastUpdate = 0
  def update() { ??? }

  protected object transforms {
    private var viewport:IntBuffer = BufferUtils.createIntBuffer(16)
    private var modelview:FloatBuffer = BufferUtils.createFloatBuffer(16)
    private var projection:FloatBuffer = BufferUtils.createFloatBuffer(16)
    private var winZ:FloatBuffer = BufferUtils.createFloatBuffer(1)
    private var winX:Float = 0
    private var winY:Float = 0
    private var position:FloatBuffer = BufferUtils.createFloatBuffer(3)

    def update() {
      GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview)
      GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection)
      GL11.glGetInteger(GL11.GL_VIEWPORT, viewport)
    }

    def toWorld(screen:vec2) :vec2 = {
      winX = screen.x.toFloat; winY = screen.y.toFloat
      GLU.gluUnProject(winX, winY, 0, modelview, projection, viewport, position)
      vec(position.get(0), position.get(1))
    }

    def fromWorld(world :vec2) :vec2 = {
      winX = world.x.toFloat; winY = world.y.toFloat
      GLU.gluProject(winX, winY, 0, modelview, projection, viewport, position)
      vec(position.get(0), position.get(1))
    }
  }
}

object View2D {
}

trait View2D extends View { view =>
  import View2D._
  type Member = Thing with Render

//  protected def zoom_=(v:R) { scale.x = v; scale.y = v }
  var zoom:R = 1
//  protected var scale:vec2 = vec2.one
  protected var rotation:Radian = 0.0f
  protected var scroll:vec2 = vec2.zero

  val __transform = Transform.dynamic(scroll, vec(zoom, zoom), rotation)

  class Layer2D(val parallax:R = 1)(protected val things:Seq[Member]) extends Layer {
    import implicits._
    val __transform = Transform.static(scale = vec(parallax, parallax) )
    def toSeq = things
  }

  def layers:IndexedSeq[Layer2D]

  def thingsNearToFar:Seq[Member] = {
    val it = layers.map(_.toSeq).reverse.flatten
    it
  }

  def render() {
    __transform {
      layers foreach { layer => layer.__render() }
    }
  }

}

///////////////////////////

trait Layer extends Render with InternalTransform {
  protected def things:Seq[Thing with Render]
  def render() {
    things foreach (_.__render())
  }
}

///////////////////////////

trait View3D extends View
trait ViewOrtho extends View3D
trait ViewPerspective extends View3D
