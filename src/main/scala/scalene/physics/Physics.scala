package scalene.physics

import scalene.core.{HashedThingStore, ThingStore, State}
import org.jbox2d.dynamics._

import org.jbox2d.common.Vec2
import scalene.{vector, common}
import common._
import math._
import vector.{vec2, vec}
import scalene.components._
import org.jbox2d.dynamics._
import org.jbox2d.collision.shapes.{PolygonShape=>b2PolygonShape, CircleShape=>b2CircleShape}
import scalene.core.traits.{Update, Hook, ScaleneMixin}


class Physics {

}

trait Box2dImplicits {
  implicit def vec2_Vec2(v:vec2) = new Vec2(v.x.toFloat, v.y.toFloat)
  implicit def Vec2_vec2(v:Vec2) = vec(v.x, v.y)
}

trait Worldly extends Update with HashedThingStore[Any] with Box2dImplicits {
  val world:World

  protected def += (p:Physical) {
    super[HashedThingStore].+=(p)
    p.__addToWorld(world)
  }

  protected def -= (p:Physical) {
    super[HashedThingStore].-=(p)
    world.destroyBody(p.theBody)
  }

  override protected def ++= (ts:Seq[Any]) {
    super.++=(ts)
    for (t <- ts) t match {
      case p:Physical => p.__addToWorld(world)
      case _ =>
    }
  }

  override protected def --= (ts:Seq[Any]) {
    super.--=(ts)
    for (t <- ts) t match {
      case p:Physical => world.destroyBody(p.theBody)
      case _ =>
    }
  }

  override def update(dt:Float) {
    world.step(dt, 10, 10)
  }

}


trait Physical extends Position2D with Rotation with Shape2D with Box2dImplicits {
  private var body:Body = null
  def theBody = body
  val initialPosition: vec2
  val initialVelocity: vec2 = vec(0,0)

  object position extends vec2 {
    def x = body.getPosition().x
    def y = body.getPosition().y
    def x_=(X:Float) { ??? }
    def y_=(X:Float) { ??? }
  }

  object velocity extends vec2 {
    def x = body.getLinearVelocity().x
    def y = body.getLinearVelocity().y
    def x_=(X:Float) { body.setLinearVelocity(new Vec2(X, this.y)) }
    def y_=(Y:Float) { body.setLinearVelocity(new Vec2(this.x, Y)) }
  }

//  def position = body.getPosition()
  def position_=(p:vec2) { body.setTransform(p, rotation.toFloat)}
//  def velocity = body.getLinearVelocity()
  def velocity_=(v:vec2) { body.setLinearVelocity(v)}
  def acceleration = ???
  def rotation = body.getAngle()
  def rotation_=(a:Float) { body.setTransform(body.getPosition(), a)}

  val setupBody: BodyDef => Unit = null
  val setupFixture: FixtureDef => Unit = null

  def __addToWorld(world:World) {

    val fixture = new FixtureDef()
    val bodydef = new BodyDef()
    val filter = new Filter()

    bodydef.`type` = BodyType.DYNAMIC
    bodydef.linearDamping = 0f
    bodydef.bullet = true
    bodydef.position = new Vec2(initialPosition)
    bodydef.linearVelocity = new Vec2(initialVelocity)

    if(setupBody!=null) setupBody(bodydef)
    body = world.createBody(bodydef)

    filter.categoryBits = 0x04
    filter.maskBits = 0xff - 1

    fixture.shape = this match {
      case s:CircleShape =>
        val circle = new b2CircleShape
        circle.m_radius = s.radius
        circle
      case s:RectangleShape =>
        val poly = new b2PolygonShape
        poly.setAsBox(s.width/2, s.height/2)
        poly
      case s:ConvexPolygonShape =>
        ???
    }

    fixture.density = 1f
    fixture.restitution = 1f
    fixture.friction = 0f
    fixture.userData = this
    if(setupFixture != null) setupFixture(fixture)
    body.createFixture(fixture)
    body
  }
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