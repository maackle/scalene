package scalene.core

import traits.{ScaleneMixin, Render}
import scalene.gfx.gl

object Op {
  def apply(fn: =>Any) = new Op(()=>{fn})
  lazy val NOOP = Op(())
}
class Op(val fn:()=>Any) {
  def apply() = fn()
}


class DrawOp(renderBlock: ()=>Unit) extends Op(renderBlock) with Render {
  def render() {
    gl.matrix {
      renderBlock()
    }
  }
}
object DrawOp {
  def apply(renderBlock: =>Unit) = new DrawOp(()=>{renderBlock})
}

class KeyOp(fn:() =>Unit) extends Op(fn)
