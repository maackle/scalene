package demos.heart.things

import scalene.core.traits.{Render, Update}
import scalene.components.RectangleShape
import scalene.vector.{vec, vec2}
import scalene.helpers.Memory
import scalene.gfx
import gfx.Color
import demos.heart.{TimeSync, states}

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 1/26/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
class HeartMonitor(val position:vec2, dimensions:(Float, Float))(implicit val state:states.Play)
  extends Render
  with Update
  with TimeSync
  with RectangleShape {

  val (width, height) = dimensions
  val periodWidth = 4f
  val dotRadius = 5f
  val lineWidth = 3f
  val color = Color.cyan

  val points = {
    val tail = 0.2f
    List(
      vec(0.0, 1),
      vec(0.15, -0.8),
      vec(0.2, tail/3),
      vec(0.25, tail),
      vec(0.4, -tail/4),
      vec(0.6, tail/5),
      vec(0.8, -tail/6),
      vec(1f, 0)
    )
  }

  val mem = new Memory[vec2](60, vec2.zero)

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
      if(p.x < q.x) {
        col.bind()
        gfx.draw.line(p,q)
      }
    }
    gfx.draw.circle(dotRadius, mem.now)
  }

  def update(dt:Float) {
    val x = (T / period) % 1
    val screenX = T % periodWidth
    for ((p, q) <- maackle.util.pairs(points)) {
      if (p.x <= x && x < q.x) {
        val pos = vec(screenX, between(p,q)(x)) * vec(width/periodWidth, height/2) - vec(width/2, 0)
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
