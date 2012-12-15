package scalene.vector

import math._
import scala.Some


object vec2 {
  def zero = new vec2(0,0)
  def one = new vec2(1,1)

  def unapply(v:vec2) = {
    Some(v.x, v.y)
  }
}

trait vec2mutable extends vec2base[V] {
  var x, y: V

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
    vec ( x*ca - y*sa,
      y*ca + x*sa )
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

  def flipX = new vec2(-x, y)
  def flipY = new vec2(x, -y)

  def +(v:vec2):vec2 = new vec2(x+v.x, y+v.y)
  def -(v:vec2):vec2 = new vec2(x-v.x, y-v.y)
  def *(c:V):vec2 = new vec2(x*c, y*c)
  def *(v:vec2):vec2 = new vec2(x*v.x, y*v.y)
  def /(c:V):vec2 = new vec2(x/c, y/c)

  @inline
  def dot(v:vec2):V = (x*v.x + y*v.y)

  @inline
  def unary_- : vec2 = new vec2(-x, -y)

  @deprecated
  def <(v:vec2) = x < v.x && y < v.y
  def <=(v:vec2) = x <= v.x && y <= v.y
  def >(v:vec2) = x > v.x && y > v.y
  def >=(v:vec2) = x >= v.x && y >= v.y

  override def toString = "vec2( %s, %s )".format(x,y)


}

class vec2(var x:V, var y:V) extends vec2mutable {
  def this(x:Double, y:Double) = this(x.toFloat, y.toFloat)
}


