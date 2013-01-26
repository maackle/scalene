package demos.heart.things

import scalene.core.traits.{Render, Update}
import scalene.components.RectangleShape
import scalene.vector.{vec, vec2}
import scalene.helpers.Memory
import scalene.gfx
import gfx.Color

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 1/26/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
class HeartMonitor(val position:vec2, dimensions:(Float, Float)) extends Render with Update with RectangleShape {

  val (width, height) = dimensions
  val periodWidth = 2.33f
  var duration = 0.9f
  val dotRadius = 5f
  val lineWidth = 3f
  val color = Color.cyan

  val points = {
    val tail = 0.2f
    List(
      vec(0.0, 0),
      vec(0.05, 1),
      vec(0.1, -0.8),
      vec(0.15, tail/3),
      vec(0.2, tail),
      vec(0.35, -tail/4),
      vec(0.55, tail/5),
      vec(0.75, -tail/6),
      vec(1f, 0)
    )
  }

  val mem = new Memory[vec2](30, vec2.zero)

  private var t:Float = 0

  def between(p1:vec2, p2:vec2) = {
    val diff = p2 - p1
    val slope = diff.y / diff.x
    val intercept = p1.y - slope * p1.x
    (x:Float) => slope * x + intercept
  }

  def render() {
    color.bind()
    gfx.draw.lineWidth(lineWidth)
    for (((p,q),i) <- maackle.util.pairs(mem.mem).view.zipWithIndex) {
      val col = color.alpha(i.toFloat / mem.mem.size)
      if(p.x <= q.x) {
        col.bind()
        gfx.draw.line(p,q)
      }
    }
    gfx.draw.circle(dotRadius, mem.now)
  }

  def update(dt:Float) {
    t += dt
    val x = (t / duration) % 1
    val T = t % periodWidth
    for ((p, q) <- maackle.util.pairs(points)) {
      if (p.x <= x && x < q.x) {
        val pos = vec(T, between(p,q)(x)) * vec(width/periodWidth, height/2) - vec(width/2, 0)
        val w = 0.4f
        val next = (pos * w) + (mem.now * (1-w))
        mem << pos
//        if(q.x < next.x)
//          mem << next
//        else
//          mem << pos
      }
    }
  }
}
