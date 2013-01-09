package demos.swifts.things

import scalene.components._
import scalene.vector.{vec2, vec}
import scalene.gfx.{Color, draw, SpriteAnimation}
import scalene.core.traits.Render


class Swift(var position:vec2) extends Birdy with Verlet2D with Rotation with Render {

  var rotation = 0.0
  var velocity = position.rotate(math.Pi/2).unit * 200
  var acceleration = vec(0,0)

  val maxVelocity = 300

  val intentionFactor = maxVelocity * 2
  val confluenceFactor = 0.5f // 0.2
  val crowdingFactor = 0f
  val repulsionFactor = 30f
  val avoidanceFactor = Hawk.maxVelocity * 4000
  val avoidancePower = 1.30

  var cell:GridCell = null

  def updateFeltForces(hawk:Hawk, arenaPadding:RectangleShape) {
    intention = {
      val c = intentionFactor
      val centerDesire = if(arenaPadding.hitTest(position)) c else c * 10
      -position / position.length * centerDesire
    }

    avoidance = {
      val diff = position - hawk.position
      val divisor = math.pow(diff.lengthSquared, avoidancePower).toFloat
      diff / divisor * avoidanceFactor
    }

    confluence = cell.avgVelocity * confluenceFactor
    crowding = (cell.avgPosition - position) * crowdingFactor

    {
      repulsion = vec2.zero
      for {
        other <- cell.neighbors if this != other
      } {
        val diff = (position - other.position)
        repulsion += diff / diff.lengthSquared
      }
      repulsion *= repulsionFactor
    }
  }

  var intention, avoidance, confluence, crowding, repulsion = vec2.zero

  def update(dt:Float) {

    rotation = velocity.angle

    acceleration = (
      intention +
        repulsion +
        confluence +
        crowding +
        avoidance
      )

    velocity = velocity.limit(maxVelocity)

  }

  def render() {
    Color.green.bind()
    draw.vector(position, intention)
    Color.blue.bind()
    draw.vector(position, confluence)
    Color.red.bind()
    draw.vector(position, avoidance)
    //      Color.cyan.bind()
    //      draw.vector(position, crowding)
  }

}