package scalene.core

import scalene.gfx._

import traits.Render
import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import scalene.common._
import scalene.misc.SolidBackground
import scalene.vector.{vec, vec2}

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

// TODO: test all of these
object View2D {

//  def simple(clearColor:Color)(things:Render*):View2D = simple(clearColor, things)
  def simple(clearColor:Color, thing:Render):View2D = simple(clearColor, Seq(thing))
  def simple(clearColor:Color, things:Seq[Render]):View2D = {
    new View2D {
      val layers = (
        Layer2D(0, SolidBackground(clearColor)) ::
        Layer2D(1, things) ::
        Nil
      )
    }
  }

  def apply(layers:Layer2D*) = {
    val ls = layers
    new View2D {
      val layers = ls
    }
  }

  def apply(clearColor:Color)(layers:Layer2D*) = {
    val ls = layers
    new View2D {
      val layers = Layer2D.apply(0, SolidBackground(clearColor)) +: ls
    }
  }

}

object Layer2D {
  def apply(parallax:Real, thing:Render):Layer2D = apply(parallax, Seq(thing))
  def apply(parallax:Real, things:Seq[Render]) = {
    new Layer2D(parallax)(things)
  }

//  def apply(parallax:Real)(things:Render*) = {
//    new Layer2D(parallax)(things)
//  }
//  def apply(parallax:Real, thing:Render) = {
//    new Layer2D(parallax)(Seq(thing))
//  }

}
class Layer2D(val parallax:Real)(protected val things:Seq[Render]) extends Layer {
  val __transform = Transform {
    gl.scale(vec(parallax, parallax))
  }
  def toSeq = things
}


trait View2D extends View { view =>

  //  protected def zoom_=(v:R) { scale.x = v; scale.y = v }
  var zoom:Real = 1
  //  protected var scale:vec2 = vec2.one
  var rotation:Radian = 0.0f
  var scroll:vec2 = vec2.zero
  private var _scale = vec2.one
  def scale = {
    _scale.x = zoom
    _scale.y = zoom
    _scale
  }
  val __transform = Transform {
    gl.translate(-scroll)
    gl.scale(scale)
    gl.rotateRad(rotation)
  }

  def layers:Seq[Layer2D]

  def thingsNearToFar:Seq[Render] = {
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
  protected def things:Seq[Render]
  def render() {
    things foreach (_.__render())
  }
}

///////////////////////////

trait View3D extends View
trait ViewOrtho extends View3D
trait ViewPerspective extends View3D
