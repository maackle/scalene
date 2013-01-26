package scalene.gfx

import scalene.vector._
import scalene.core.traits.Render
import scalene.components.Position2D

object Transform {
  def apply(fn: =>Unit) = new Transform {
    def inject = fn
  }
}
trait Transform { self =>
  @inline def inject()
  @inline def apply(bloc: =>Unit) = {
    gl.matrix {
      inject()
      bloc
    }
  }
  def &(other:Transform) = new Transform {
    def inject() {
      self.inject()
      other.inject()
    }
  }
}

trait InternalTransform extends Render {
  protected def __transform:Transform
  abstract override def __render() {
    if(__transform!=null) __transform.apply { super.__render() }
    else super.__render()
  }
}

trait AutoTransformer2D extends InternalTransform with Position2D {
  def translate: vec2 = position
  def scale: vec2
  def rotation: Radian

  protected var __transform = Transform {
    gl.translate(translate)
    gl.scale(scale)
    gl.rotateRad(rotation)
  }


}
