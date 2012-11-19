package scalene.core

import traits.{Thing, InternalTransform, Render}
import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import scalene.vector._
import org.lwjgl.util.glu.GLU
import scalene.gfx.{Transform2D, Transform, Color}
import scalene.misc.SolidBackground
import scalene.common._

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

trait ViewScheme extends Render

object ViewScheme {
  def simple(bg:Color, things:Seq[Thing with Render]) = new ViewSingle2D {
    val view = new View2D {
      val layers = Vector(
        new Layer2D(0)(new SolidBackground(bg) :: Nil),
        new Layer2D(1)(things)
      )
    }
  }
}

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



object View2D {
}

trait View2D extends View { view =>
  import View2D._
  type Member = Thing with Render

  //  protected def zoom_=(v:R) { scale.x = v; scale.y = v }
  var zoom:Real = 1
  //  protected var scale:vec2 = vec2.one
  protected var rotation:Radian = 0.0f
  protected var scroll:vec2 = vec2.zero
  private var _scale = vec2.one
  def scale = {
    _scale.x = zoom
    _scale.y = zoom
    _scale
  }
  val __transform = Transform.dynamic(()=>scroll, scale _, ()=>rotation)

  class Layer2D(val parallax:Real = 1)(protected val things:Seq[Member]) extends Layer {
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
