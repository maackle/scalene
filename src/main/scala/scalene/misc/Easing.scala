package scalene.misc

import scalene.core.HashedThingStore
import scalene.core.traits.Update
import scalene.common
import scalene.misc.Easing.Ops


object Easing {

  lazy val linear = Easing(t => t)
  lazy val quadratic = Easing(t => t*t)
  lazy val cubic = Easing(t => t*t*t)
  def exponential(base:Double) = Easing(t => {
    ( (math.pow(base, t)-1) / (base-1)).toFloat
  } )
  lazy val sine = Easing(t => math.sin(t*math.Pi).toFloat)

  private[scalene] trait Ops[A] {
    def +(a:A):A; def /(a:Float):A
  }

//  def custom[A](fn:(Float)=>A) = new Easing {
//    val f = fn
//  }
//
//  def animate[A <: AnyRef](x:A)(f:Unit) {}
}

case class Easing(fn: Float=>Float) {

  def apply(t:Float) = { fn(t) }
  def interpolate[A <: Ops[A]](a:A, z:A)(t:Float) = (a + z) / fn(t)
  def inverse = Easing( 1f - fn(_) )
}

trait Transition
extends Update {

  def duration:Float
  def fn:Float=>Unit
  def store:HashedThingStore

  def now() = common.seconds
  private var startTime:Float = 0
  private var pauseTime:Float = 0
  private var pauseLength:Float = 0
  protected var progress:Float = 0
  private var playing:Boolean = false
  private var looping:Boolean = false
  private var doneFn:()=>Unit = common.NOOP

  private def reset() = {
    startTime = now()
    pauseLength = 0
    progress = 0
  }

  def start() = {
    reset()
    playing = true
    store += this
    this
  }

  def pause() = {
    playing = false
    pauseTime = now()
  }

  def resume() = {
    playing = true
    pauseLength += (now() - pauseTime)
    this
  }

  def stop() = {
    reset()
    playing = false
    store -= this
    this
  }

  def loop(yes_? : Boolean) = {
    looping = yes_?
    this
  }

  def onDone(bloc: =>Unit) = {
    doneFn = () => { bloc }
  }

  def update(--- : Float) {
    if(playing) {
      if(progress <= 1f) {
        fn(progress)
        progress = (now() - startTime - pauseLength) / duration
      }
      if(progress > 1f) {
        if(looping) start()
        else stop()
        doneFn()
      }
    }
  }
}