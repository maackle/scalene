package scalene.gfx

import scalene.vector._
import org.lwjgl.opengl.GL11
import org.lwjgl.BufferUtils
import scalene.common


object Transform {

  def dynamic(translate: =>vec2=vec2.zero, scale: =>vec2=vec2.one, rotate: =>Radian=0) = {
    new Transformer2D(()=>translate, ()=>scale, ()=>rotate)
  }
  def static(translate: vec2=vec2.zero, scale: vec2=vec2.one, rotate: Radian=0) = {
    new Transform2D(translate, scale, rotate)
  }
  @inline def immediately(translate: vec2=vec2.zero, scale: vec2=vec2.one, rotate: Radian=0)(bloc: =>Unit) {
    gl.matrix {
      inject(translate, scale, rotate)
      bloc
    }
  }
  @inline def inject(translate: vec2, scale: vec2, rotate: Radian) {
    if(rotate != 0) println(rotate)
    gl.translate(translate)
    gl.rotate(rotate)
    gl.scale(scale)
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

trait TransformAffine extends Transform {
  def translate: vector
  def scale: vector
  def rotate: Radian
}

trait TransformerAffine extends Transform {

  def translate: ()=>vector
  def scale: ()=>vector
  def rotate: ()=>Radian
}

object Transform2D {
  lazy val identity = new Transform2D(vec2.zero, vec2.one, 0)
}

class Transform2D(val translate: vec2, val scale: vec2, val rotate: Radian) extends TransformAffine {

  import common.implicits.fn2val

  def inject() = Transform.inject(translate, scale, rotate)

}


object Transformer2D {
  lazy val identity = new Transformer2D(()=>vec2.zero, ()=>vec2.one, ()=>0)
}

class Transformer2D(val translate: ()=>vec2, val scale: ()=>vec2, val rotate: ()=>Radian) extends TransformerAffine {

  import common.implicits.fn2val

  def inject() = Transform.inject(translate(), scale(), rotate())

}
