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
import org.jbox2d.collision.shapes.{PolygonShape => b2PolygonShape, CircleShape => b2CircleShape, MassData}
import scalene.core.traits.{Update, Hook, ScaleneMixin}


class Physics {

}

trait Box2dImplicits {
  implicit def vec2_Vec2(v:vec2) = new Vec2(v.x.toFloat, v.y.toFloat)
  implicit def Vec2_vec2(v:Vec2) = vec(v.x, v.y)
}

trait Worldly extends Update with HashedThingStore with Box2dImplicits {
  val world:World

  protected def += (p:Physical) {
    super[HashedThingStore].+=(p)
    p.__addToWorld(world)
  }

  protected def -= (p:Physical) {
    super[HashedThingStore].-=(p)
    world.destroyBody(p.body)
  }

  override protected[scalene] def ++= (ts:Seq[Any]) {
    super.++=(ts)
    for (t <- ts) t match {
      case p:Physical => p.__addToWorld(world)
      case _ =>
    }
  }

  override protected[scalene] def --= (ts:Seq[Any]) {
    super.--=(ts)
    for (t <- ts) t match {
      case p:Physical => world.destroyBody(p.body)
      case _ =>
    }
  }

  override def update(dt:Float) {
    world.step(dt, 10, 10)
  }

}

trait PimpedBody extends Box2dImplicits {
  def body:Body

//  private val massData = new MassData()
//  def mass = body.getMass
//  def mass_=(m:Float)= {
//    massData.mass = m
//    body.setMassData(massData)
//  }
  def limitLinearVelocity(speed:Float) = {
    val vel = body.getLinearVelocity
    val len = vel.length
    if(len > speed)
      body.setLinearVelocity(vel * speed / len)
  }
  def limitAngularVelocity(speed:Float) = {
    if(math.abs(body.getAngularVelocity) > speed)
      body.setAngularVelocity(speed)
  }
}


trait Physical extends Position2D with Rotation with Shape2D with Box2dImplicits {
  private var _body:Body = null
  def body = _body
  val initialPosition: vec2
  val initialVelocity: vec2 = vec(0,0)

  implicit def _pimped_body(b:Body) = new PimpedBody { val body = b }

  object position extends vec2 {
    def x = _body.getPosition().x
    def y = _body.getPosition().y
    def x_=(X:Float) { ??? }
    def y_=(X:Float) { ??? }
  }

  object velocity extends vec2 {
    def x = _body.getLinearVelocity().x
    def y = _body.getLinearVelocity().y
    def x_=(X:Float) { _body.setLinearVelocity(new Vec2(X, this.y)) }
    def y_=(Y:Float) { _body.setLinearVelocity(new Vec2(this.x, Y)) }
  }

//  def position = body.getPosition()
  def position_=(p:vec2) { _body.setTransform(p, rotation.toFloat)}
//  def velocity = body.getLinearVelocity()
  def velocity_=(v:vec2) { _body.setLinearVelocity(v)}
  def acceleration = ???
  def rotation = _body.getAngle()
  def rotation_=(a:Float) { _body.setTransform(_body.getPosition(), a)}

  private var fnBodyDef:(BodyDef=>Unit) = NOOP1
  private var fnFixtureDef:(FixtureDef=>Unit) = NOOP1
  private var fnBody:(Body=>Unit) = NOOP1

  protected def setupBodyDef(fn:(BodyDef=>Unit)) = {
    fnBodyDef = fn
  }
  protected def setupFixtureDef(fn:(FixtureDef=>Unit)) = {
    fnFixtureDef = fn
  }
  protected def setupBody(fn:(Body=>Unit)) = {
    fnBody = fn
  }
  protected def setup(
                       bodydef:(BodyDef=>Unit) = NOOP1,
                       fixturedef:(FixtureDef=>Unit) = NOOP1,
                       body:(Body=>Unit) = NOOP1
                       ) = {
    fnBodyDef = bodydef
    fnFixtureDef = fixturedef
    fnBody = body
  }

  private[scalene] def __addToWorld(world:World) {

    val fixture = new FixtureDef()
    val bodydef = new BodyDef()
    val filter = new Filter()

    bodydef.`type` = BodyType.DYNAMIC
    bodydef.linearDamping = 0f
    bodydef.angularDamping = 0f
//    bodydef.bullet = true
    bodydef.position = new Vec2(initialPosition)
    bodydef.linearVelocity = new Vec2(initialVelocity)
    fnBodyDef(bodydef)

    _body = world.createBody(bodydef)

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
    fnFixtureDef(fixture)

    _body.createFixture(fixture)
    fnBody(body)
    _body
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