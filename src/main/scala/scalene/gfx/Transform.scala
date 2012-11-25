package scalene.gfx

import scalene.common._
import scalene.vector._
import scalene.core.traits.{Render}

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
  def __transform:Transform
  override def __render() {
    __transform.apply { super.__render() }
  }
}

trait AutoTransformer2D extends Render {
  def translate: vec2
  def scale: vec2
  def rotation: Radian

  override def __render() {
    gl.matrix {
      gl.translate(translate)
      gl.scale(scale)
//      gl.rotateRad(rotation)
      super.__render()
    }
  }

}
