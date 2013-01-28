package demos.heart.things

import scalene.core.traits.{Render, Update}
import scalene.components.RectangleShape
import scalene.vector.{vec, vec2}
import scalene.helpers.Memory
import scalene.gfx
import gfx.Color
import demos.heart.{TimeSync, states}
import maackle.util.Random

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
  val periodWidth = 2f // not what you expect...
  val lineWidth = 4f
  val dotRadius = lineWidth/2
  val color = Color(0x800000)
  var aberration:Float = 0f
  val maxAmp = 0.9f

  val points = {
    val tail = 0.2f
    List(
      vec(0.01, 1),
      vec(0.05, -0.8),
      vec(0.1, tail/3),
      vec(0.15, tail/2),
      vec(0.3, -tail/4),
      vec(0.5, tail/5),
      vec(0.7, -tail/6),
      vec(0.95, 0),
      vec(1.0, 1)
    ).map(_ * maxAmp)
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
    gfx.draw.fill(true)
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
    val x = (T / period-latencyCorrection) % 1
    val screenX = T % periodWidth
    for ((p, q) <- maackle.util.pairs(points)) {
      var (px, py) = (p.x, p.y)
      var (qx, qy) = (q.x, q.y)
      if (qx < px) qx += period
      if (px <= x && x < qx) {
        val fudge = aberration / 10f
        val screenY = math.min(maxAmp, between(p,q)(x + fudge*fudge))
        val pos = vec(screenX, screenY) * vec(width/periodWidth, height/2) - vec(width/2, 0)
        val w = 0.4f
        mem << pos
//        if(q.x < next.x)
//          mem << next
//        else
//          mem << pos
      }
    }
  }
}
