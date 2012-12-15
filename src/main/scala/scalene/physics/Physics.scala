package scalene.physics

import scalene.core.State
import org.jbox2d.dynamics.{BodyType, Body, World}

import org.jbox2d.common.Vec2
import scalene.{vector, common}
import common._
import math._
import scalene.vector.{vec, vec2, vec2base}


class Physics {

}


trait Worldly {
  implicit def vec2_Vec2(v:vec2) = new Vec2(v.x.toFloat, v.y.toFloat)
  implicit def Vec2_vec2(v:Vec2) = vec(v.x, v.y)

  val world:World


}

case class FixtureFactory(
                           )

case class BodyFactory(
  `type`: BodyType = BodyType.STATIC,
  position: vec2,
  bullet: Boolean,
  linearDamping: Float


                        )

trait Bodily {
  def body:Body

}