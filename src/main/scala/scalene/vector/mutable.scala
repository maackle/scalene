package scalene.vector

import scala.math._

import scalene.vector.{vec2=>v2}

object mutable /*extends Mutability*/ {
  lazy val zero = new vec2(0,0)
  lazy val one = new vec2(1,1)
  class vec2(var x:V, var y:V) extends vec2base {
    val mu = scalene.vector.mutable
    def immutable = new scalene.vector.immutable.vec2(x,y)
    @inline
    def +=(v:vec2) { x+=v.x; y+=v.y }
    @inline
    def -=(v:vec2) { x-=v.x; y-=v.y }
    @inline
    def *=(v:vec2) = {
      x *= v.x
      y *= v.y
    }
    @inline
    def *=(c:Float) = {
      x *= c
      y *= c
    }
    @inline
    def set(v:vec2) {
      x = v.x
      y = v.y
    }
  }
}
