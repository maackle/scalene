package scalene

import core.Op
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import grizzled.slf4j.Logger
import org.lwjgl.Sys

package object common {

  type Real = Float
  type Radian = Double

  val NOOP = () => {}
  def NOOP1[T] = (t:T) => {}
  def NOOP2[T, U] = (t:T, u:U) => {}

  private var __onetimer = collection.mutable.Set[Op]()

  private lazy val ms0 = System.currentTimeMillis()

  def milliseconds: Long = {
    (System.currentTimeMillis() - ms0)
  }

  def seconds: Float = milliseconds / 1000f

  object implicits {
    implicit def block2Fn(bloc: =>Any) = ()=>{bloc}
    implicit def fn2val[A](fn: ()=>A):A = fn()
  }

  def once(bloc: =>Unit) = {
    val op = Op(bloc)
    if(! __onetimer.contains(op)) {
      op()
      __onetimer += op
    }
  }

  def ??? = throw new NotImplementedException
  def !!! = throw new Exception("This should not happen!!!")
  def TODO(s:String) = once {
    Logger("scalene").info("TODO: %s" format s)
  }

  def deg2rad(deg:Real) = deg * math.Pi / 180
  def rad2deg(rad:Real) = rad * 180 / math.Pi

  def px(a:Real):Real = 1 / 100 * a
}