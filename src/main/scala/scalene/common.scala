package scalene

import core.Op
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import grizzled.slf4j.Logger
import org.lwjgl.Sys

package object common {

  type Real = Double

  private var __onetimer = collection.mutable.Set[Op]()

  private def _milliseconds = ((Sys.getTime * 1000) / Sys.getTimerResolution)
  private lazy val _milliseconds0 = _milliseconds
  def milliseconds: Double = {
    _milliseconds - _milliseconds0
  }

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

  def px(a:Double):Real = 1 / 100 * a
}