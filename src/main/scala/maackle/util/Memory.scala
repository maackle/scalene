package scalene.helpers

import Ordering.Implicits._
import Numeric.Implicits._
import collection.mutable


class Memory[T](val size: Int, default: T) {
  protected var history = mutable.Queue[T]()

  def mem = history.toList

  for (i <- 1 to size + 1) history.enqueue(default)

  def now = history.last

  def prev = history(size - 1)

  protected def x = now

  protected def y = prev

  def <<(v: T) = {
    history.enqueue(v)
    history.dequeue()
  }

  protected def update(v: T) = <<(v)

  override def toString = "[Stateful (now=%s prev=%s)(%s)]".format(now, prev, history)
}


case class MemBoolean(override val size: Int) extends Memory(size, false) {
  def xor = x && !y || y && !x

  def xOn = x && !y

  def xOff = y && !x
}

@specialized
class MemoryNumeric[T: Numeric](size: Int, default: T) extends Memory[T](size, default) {
  def sum = history.reduce(_ + _)

  def min = history.reduce(_ min _)

  def max = history.reduce(_ max _)
}

case class MemInt(override val size: Int) extends MemoryNumeric(size, 0) {
  def avg = sum / history.length
}

case class MemFloat(override val size: Int) extends MemoryNumeric(size, 0f) {
  def avg = sum / history.length
}

case class MemDouble(override val size: Int) extends MemoryNumeric(size, 0.0) {
  def avg = sum / history.length
}

