package scalene.core

import java.io.InputStream
import maackle.util._


object Resource {
  private val all = collection.mutable.Set[Resource[_]]()

//  def apply[T](path: String)(fn: (InputStream) => T) { apply(getStream(path))(fn) }
  def apply[T](path: String)(fn: (String) => T) = {
    val res = new Resource[T](path, fn)
    all += res
    res
  }
  def loadAll() {
    all foreach (_.load())
  }
}

class Resource[T](path: String, fn: (String) => T) {
  private var x:T = _
  def is = { assume(x != null); x }
  def load() {
    x = fn(path)
  }
}
