package scalene.vector


object vec2 {
  def zero = new vec2(0,0)
  def one = new vec2(1,1)

  def unapply(v:vec2) = {
    Some(v.x, v.y)
  }
}

class vec2(var x:V, var y:V) extends vec2base {

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
  def *=(c:Double) = {
    x *= c
    y *= c
  }
  @inline
  def set(v:vec2) {
    x = v.x
    y = v.y
  }
}


