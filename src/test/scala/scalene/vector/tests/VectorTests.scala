package scalene.vector

import org.scalatest.FunSuite
//import scalene.vector.vec

trait VectorTests extends FunSuite {

  test("equality") {
    val v:vec2 = vec(1,1)
//    val v:vec3 = vec(1,1,1)

    assert(v - vec2.one === vec2.zero)
  }

}