package scalene.gfx.particles

import scalene.common._
import scalene.components.{Verlet2D, Position2D, Acceleration2D}
import scalene.core.traits.{Update, Render}
import scalene.vector.vec2

trait ParticleSystem extends Update with Render with Position2D {

  private val particlePool = Array.ofDim[Particle](numParticles)

  def numParticles:Int
  def emitters:Traversable[Emitter]
  def particles:Traversable[Particle]

  def update(dt:Float) {
    emitters.map { emitter =>
      emitter.__update()
    }
  }
}

trait Emitter {

  private var lastEmission = milliseconds

  def system:ParticleSystem

  def frequency:Real
  def emit()
  def update()
  def __update() {
    update()
    if((milliseconds - lastEmission) > 1000/frequency) {
      lastEmission = milliseconds
      emit()
    }
  }

}

class Particle(var position:vec2, var velocity:vec2, var acceleration:vec2) extends Verlet2D {
  override def update(dt:Real) { !!! }
}