package maackle

import math._
import scala.Numeric
import org.newdawn.slick.util.ResourceLoader
import org.lwjgl.Sys

package object util {

  def mano[T: Manifest](t: T): Manifest[T] = manifest[T]

  def getStream(path: String) = ResourceLoader.getResourceAsStream(path)

  def getFile(path: String) = io.Source.fromInputStream(getStream(path))

  // Must be Double!  If using float, subtract a very large number first
  def getMilliseconds: Double = {
    ((Sys.getTime * 1000) / Sys.getTimerResolution)
  }

  @inline
  def clamp(value: Float, low: Float, high: Float): Float = {
    if (value < low) low else if (value > high) high else value
  }

  @inline
  def linspace(lo: Float, hi: Float, n: Int): Seq[Float] = {
    for (i <- 0 until n) yield {
      i / (n - 1f) * (hi - lo) + lo
    }
  }

  def pairs[T](seq: Seq[T]) = {
    if (seq.size > 1)
      seq.slice(0, seq.size - 2).zip(seq.slice(1, seq.size - 1))
    else
      throw new Exception("must have at least 2 elements to form pairs")
  }

  def pairwise[T](seq: Seq[T])(fn: (T, T) => Unit) {
    seq.reduceLeft {
      (a: T, b: T) => {
        fn(a, b)
        b
      }
    }
  }

  object Radian {
    val pi = math.Pi
    val pi2 = math.Pi * 2

    def clampS(in: Double) = {
      var a = in
      while (a > pi) a -= pi2
      while (a <= -pi) a += pi2
      a
    }

    def clampU(in: Double) = {
      var a = in
      while (a > pi2) a -= pi2
      while (a < 0) a += pi2
      a
    }

    def diff(a: Double, b: Double) = {
      clampS(clampS(a) - clampS(b))
    }
  }

  def xprint[T](value: => T): T = xprint()(value)

  @deprecated("Grow up and use Logging")
  def xprint[T](lbl: String = "xprint")(value: => T): T = {
    print("%s: ".format(lbl));
    val v: T = value;
    println(v);
    v
  }

  def yes[T](block: => T) = {
    block
  }

  def no[T](block: => T) {}

  def lerp[T: Numeric](a: (T), b: (T), t: Float, p: Double = 1.0): Float = {
    val imp = implicitly[Numeric[T]]
    val fa = imp.toFloat(a)
    val u = if (p == 1.0) t else math.pow(t, p).toFloat
    fa + (imp.toFloat(b) - fa) * u
  }

  def invlerp[T: Numeric](a: (T), b: (T), t: Float): Float = {
    val imp = implicitly[Numeric[T]]
    val fa = imp.toFloat(a)
    (t - fa) / (imp.toFloat(b) - fa)
  }

  def abspow(x: Double, p: Double): Double = {
    signum(x) * pow(abs(x), p)
  }

  trait Validates {
    def valid(): Boolean
  }

  // pathPattern(dir, name, ext)
  val pathPattern = """(.*)/(.+?)\.(.+?)$""".r

  def assertValid[A <: {def isValid:Boolean}, T](objs: A*)(block: => T): T = {
    for ((o, i) <- objs.view.zipWithIndex)
      if (!o.isValid) throw new Exception("Validity check #%d failed at the beginning".format(i + 1))
    val ret = block
    for ((o, i) <- objs.view.zipWithIndex)
      if (!o.isValid) throw new Exception("Validity check #%d failed at the end".format(i + 1))
    ret
  }

}