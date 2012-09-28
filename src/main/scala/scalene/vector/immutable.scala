package scalene.vector

import scala.math._

import scalene.vector.{vec2=>v2}

object immutable /* extends Mutability*/ {
  lazy val zero = new vec2(0,0)
  lazy val one = new vec2(1,1)

  case class vec2(x:V,y:V) extends vec2base {
    val mu = scalene.vector.immutable

    def mutable = new scalene.vector.mutable.vec2(x,y)
    override def hashCode = ((x+31) * 31 + y).toInt

    def unapply(data:Any):Option[(V, V)] = data match {
      case v:vec2base => Some(v.x, v.y)
      case _ => None
    }
  }

  
}
