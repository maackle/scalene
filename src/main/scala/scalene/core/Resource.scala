package scalene.core

import scalene.common
import grizzled.slf4j.{Logging, Logger}
import scala.None
import scalene.gfx.{Tex, ImageLike, Image}

trait Resource[O] {
  def locator: Resource.Locator
//  def fn: (I) => O
//  def is:O
//  def load()
  protected def loadFn: Resource.Locator => O

  override def equals(o:Any) = {
    o match {
      case r:Resource[_] => r.locator == this.locator
      case _ => false
    }
  }

  def map[A](fn:(O=>A)) = {
    Resource(locator)(loc => {
      fn(loadFn(loc))
    })
  }

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
    x = loadFn(locator)
    isLoaded = true
  }
  override def toString = "Resource(%s)".format(locator)
}

//trait ImageResource extends Resource[Image] with ImageLike {
//  def tex = is.tex
//  def clip = is.clip
//  def width = is.width
//  def height = is.height
//  def render() { is.render() }
//}

object Resource extends Logging {

  type Locator = String
  private val needsLoading = collection.mutable.Set[Resource[_]]()
  private val registered = collection.mutable.Map[Locator, Resource[_]]()

  private var autoload_? = false
  def beginAutoloading() {
    autoload_? = true
    loadAll()
  }

  def tryAutoload(res:Resource[_]) {
    if(autoload_?) {
      res.load()
      info("resource %s loaded immediately" format res.locator)
    }
    else {
      needsLoading += res
      info("resource %s ready to be loaded" format res.locator)
    }
  }

  private def lookup[O](locator: Locator)(fn: (Locator) => O):Resource[O] = {
    val key = (locator)
    registered.get(key) match {
      case Some(res) =>
        warn("resource already registered: " + locator)
        res.asInstanceOf[Resource[O]]
      case None =>
        val vs = (locator, fn)
        val res = new Resource[O] {
          val (locator, loadFn) = vs
        }
        registered += locator -> res
        res
    }
  }

//  def apply[T](path: String)(fn: (InputStream) => T) { apply(getStream(path))(fn) }
//  def apply[O](path:String)(fn: Locator[String] => O)

//  protected def make[O, R <: Resource[O]](locator: Locator)(fn: (Locator) => O):R = {
//
//    val res = lookup(locator)(fn)
//    info("creating resource: " + res.locator)
//    tryAutoload(res)
//    res
//  }

  def apply[O](locator: Locator)(fn: (Locator) => O):Resource[O] = {
    val res = lookup(locator)(fn)
    info("creating resource: " + res.locator)
    tryAutoload(res)
    res
  }

  def loadAll() {
    info("loading remaining resources..." + needsLoading.map(_.locator))
    needsLoading foreach { r =>
      r.load()
    }
  }

//  class ResourceImpl[O](val locator: Locator, protected val loadFn: (Locator) => O) extends Resource[O] with Logging {
//    private var x:O = _
//    private var isLoaded = false
//    def is:O = {
//      if(!isLoaded) {
//        Logger("Resource").error("Bad Resource access")
//        throw new Exception("Bad Resource access")
//      }
//      require(isLoaded, "attempted to access resource before it was loaded")
//      assume(x != null)
//      x
//    }
//    def map = common.???
//    def load() {
//      x = loadFn(locator)
//      isLoaded = true
//    }
//    override def toString = "Resource(%s)".format(locator)
//  }

}
