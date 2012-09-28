package scalene

import core.Op
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import grizzled.slf4j.Logger
import org.lwjgl.Sys

object common {

  type R = Double

  private var __onetimer = collection.mutable.Set[Op]()

  def milliseconds: Double = {
    ((Sys.getTime * 1000) / Sys.getTimerResolution)
  }

  object implicits {
    implicit def block2Fn(bloc: =>Any) = ()=>{bloc}
    implicit def fn2val[A](fn: ()=>A):A = fn()
  }

  def once(bloc: =>Unit) = {
    val op = Op(bloc)
    if(__onetimer contains op) {
      op()
      __onetimer += op
    }
  }

  def ??? = throw new NotImplementedException
  def !!! = throw new Exception("This should not happen!!!")
  def TODO(s:String) = once {
    Logger("scalene").info("TODO: %s" format s)
  }

  def px(a:Double):R = 1 / 100 * a
}