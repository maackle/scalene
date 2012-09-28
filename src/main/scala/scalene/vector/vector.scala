package scalene

package object vector {

  trait Mutability {

  }

  trait vector

//  val kind:Mutability = scalene.vector.mutable
  type vec2 = scalene.vector.mutable.vec2

  type V = Double
  type Radian = Double

  val eps:V = 0.001f

//  implicit def b2vec2_vec2(v:b2vec2):vec2 = new vec2(v.x, v.y)

  implicit def tuple_vec2m(x:(V, V)):mutable.vec2 = new mutable.vec2(x._1, x._2)
  implicit def tuple_vec2i(x:(V, V)):immutable.vec2 = new immutable.vec2(x._1, x._2)

//  implicit def vec3_tuple(v:vec3):(V, V, V) = (v.x, v.y, v.z)
//  implicit def tuple_vec3(x:(V, V, V)) = new vec3(x._1, x._2, x._3)

}