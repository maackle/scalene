package scalene.traits

import scalene.gfx.Color
import scalene.gfx
import collection.mutable.ListBuffer
import collection.mutable


trait Node {
  protected lazy val children:mutable.Set[Node] = mutable.Set[Node]()
  def +=(n:Node) { children += n }
  def -=(n:Node) { children -= n }
}

trait Thing

trait Entity extends Thing with Update with Render

object ID {
  private var lastId=1000
  private def nextId = { lastId+=1; lastId }
}

trait ID {
  val id = ID.nextId
}




