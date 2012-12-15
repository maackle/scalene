package scalene.vector

import math._

trait vec2base[@specialized(Float, Double) V] extends vec {

//  val fractOps = implicitly[Fractional[V]]
//  import fractOps._
  var x : V
  var y : V

  override def equals(other: Any):Boolean = {
    other match {
      case v:vec2 => x == v.x && y == v.y
      case _ => false
    }
  }

  // hashMap is defined only for the immutable class


  def nonZero = x != 0 || y != 0

  def length:V

  def tuple = (x,y)

  def project(other:vec2):vec2

  def unit:vec2

  def limit(cap:V):vec2

  @inline
  def angle:Float

  def flipX:vec2
  def flipY:vec2

  def +(v:vec2):vec2
  def -(v:vec2):vec2
  def *(c:V):vec2
  def *(v:vec2):vec2
  def /(c:V):vec2

  @inline
  def dot(v:vec2):V

  @inline
  def polar = (length,angle)

  @inline
  def unary_- : vec2

}
