package maackle.util

case class Countdown(start: Int, decrement: Int = 1, looping: Boolean = false) {
  private var time = 0
  private var time2 = 0

  def set() {
    if (isExpired)
      forceSet()
  }

  def forceSet() {
    time = start
    time2 = start
  }

  def tick() {
    time2 = time
    time -= decrement
    if (looping && time < 0) {
      time = start
    }
  }

  def isExpired = time <= 0

  def justStarted = time2 == start && time != start

  @deprecated("Untested")
  def justLooped = time2 <= 0 && time > 0

  def justEnded = time <= 0 && time2 > 0

  def unit = {
    val r = time.toFloat / start.toFloat
    assert(r <= 1)
    clamp(r, 0, 1)
  }

  def t = time
}
