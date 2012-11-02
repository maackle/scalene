package scalene.core

import java.io.{File, InputStream}
import maackle.util._
import scalene.common
import grizzled.slf4j.{Logging, Logger}
import org.newdawn.slick.opengl.Texture

trait Resource[I,O] {
  def locator: I
//  def fn: (I) => O
  def is:O
  def load()
}

//TODO: cache file names
object Resource extends Logging {
  private val all = collection.mutable.Set[Resource[_,_]]()

  private var autoload_? = false
  def startAutoload(do_? : Boolean) { autoload_? = do_? }

  def tryAutoload(res:Resource[_,_]) {
    if(autoload_?) {
      res.load()
      debug("resource %s loaded immediately" format res.locator)
    }
    else {
      all += res
      debug("resource %s ready to be loaded" format res.locator)
    }
  }

//  def apply[T](path: String)(fn: (InputStream) => T) { apply(getStream(path))(fn) }
//  def apply[O](path:String)(fn: Locator[String] => O)

  def apply[I,O](path: I)(fn: (I) => O) = {
    val res = new ResourceImpl[I,O](path, fn)
    info("creating resource: " + res.locator)
    tryAutoload(res)
    res
  }

  def loadAll() {
    info("loading remaining resources..." + all.map(_.locator))
    all foreach (_.load())
  }

  class ResourceImpl[I,O](val locator: I, fn: (I) => O) extends Resource[I,O] with Logging {
    private var x:O = _
    private var isLoaded = false
    def is:O = {
      if(!isLoaded) {
        Logger("Resource").error("Bad Resource access")
        throw new Exception("Bad Resource access")
      }
      require(isLoaded, "attempted to access resource before it was loaded")
      assume(x != null)
      x
    }
    def map = common.???
    def load() {
      x = fn(locator)
      isLoaded = true
    }
  }



  //  class Locator[I]
//
//  class LocatorPath extends Locator[String]
//  class LocatorFile extends Locator[File]
}
