package scalene.vector

import org.scalatest.FunSuite

trait VectorTests extends FunSuite {

  test("equality") {
    val v:vec2 = vec(1,1)

    assert(v - vec2.one === vec2.zero)
  }

}