package scalene.vector

import math._
import scala.Some


object vec2 {
  def zero = vec2(0,0)
  def one = vec2(1,1)

  def apply(X:Double, Y:Double) = new vec2 {
    var x = X.toFloat
    var y = Y.toFloat
  }
//
//  def unapply(v:vec2) = {
//    Some(v.x, v.y)
//  }
}

trait vec2 extends vec2base {
  def x: V
  def y: V
  def x_=(X:V)
  def y_=(Y:V)

  def isValid:Boolean = !x.isNaN && !y.isNaN

  @inline
  def lengthSquared = x*x + y*y
  @inline
  def length = sqrt(lengthSquared).toFloat
  @inline
  def manhattan = math.abs(x) + math.abs(y)

  def rotate(rad:Radian):vec2 = {
    val ca = cos(rad)
    val sa = sin(rad)
    vec (
      x*ca - y*sa,
      y*ca + x*sa
    )
  }

  def clear() {
    x = 0
    y = 0
  }

  def project(other:vec2):vec2 = {
    val denom = (other dot other)
    if(denom < eps) return vec2.zero
    else other * ((this dot other)/denom)
  }

  def unit:vec2 = {
    val len = length
    if(len < eps || len.toFloat.isNaN) vec2.zero
    else this / len
  }

  def limit(cap:V):vec2 = {
    val len = length
    if (len > cap && !len.toFloat.isNaN) {
      vec(
        x * cap/len,
        y * cap/len
      )
    }
    else vec(x,y) // FIXME unnecessary copy
  }

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
  def *=(c:V) = {
    x *= c
    y *= c
  }
  @inline
  def set(v:vec2) {
    x = v.x
    y = v.y
  }

  @inline
  def angle:Float = {
    if(x!=0 || y!=0) atan2(y,x).toFloat else 0
  }

  def flipX = vec2(-x, y)
  def flipY = vec2(x, -y)

  def +(v:vec2base):vec2 = vec2(x+v.x, y+v.y)
  def -(v:vec2base):vec2 = vec2(x-v.x, y-v.y)
  def *(c:V):vec2 = vec2(x*c, y*c)
  def *(v:vec2base):vec2 = vec2(x*v.x, y*v.y)
  def /(c:V):vec2 = vec2(x/c, y/c)

  @inline
  def dot(v:vec2):V = (x*v.x + y*v.y)

  @inline
  def unary_- : vec2 = vec2(-x, -y)

  @deprecated
  def <(v:vec2) = x < v.x && y < v.y
  def <=(v:vec2) = x <= v.x && y <= v.y
  def >(v:vec2) = x > v.x && y > v.y
  def >=(v:vec2) = x >= v.x && y >= v.y

  override def toString = "vec2( %s, %s )".format(x,y)


}

//class vec2(var x:V, var y:V) extends vec2 {
//  def this(x:Double, y:Double) = this(x.toFloat, y.toFloat)
//}
