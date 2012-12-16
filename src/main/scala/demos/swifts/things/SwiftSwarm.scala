package demos.swifts.things

import scalene.gfx._
import org.lwjgl.opengl.GL11
import scalene.vector.{vec2, vec}
import scalene.core.ThingStore
import demos.swifts.TheSwifts
import scalene.common
import scalene.misc.ScaleneGrid
import collection.mutable.ListBuffer
import com.tinyline.tiny2d.i
import scalene.components.RectangleShape

class SwiftSwarm(size:Int) extends TriangleBatch {

  class GridCell {
//    val velocity = vec2.zero
    val swifts = ListBuffer[Swift]()

    def clear() {
//      velocity.clear()
      swifts.clear()
    }
  }

  val arenaDim = 2000f

  def app = TheSwifts

  val N = size * 3

  val grid = ScaleneGrid.square(arenaDim, 20, vec(0,0))(new GridCell)

  val shape = {
    val verts = 3
    val sz = 4
    (for (t <- List(0.0f, 1.1f, 1.9f)) yield {
      vec.polar(sz, t * (2*math.Pi) / verts)
    }).toArray
  }

  val swifts = for (i <- 1 to size) yield {
    new Swift(vec.polar.random(100))
  }

  val arena = new scalene.components.RectangleShape {
    val width, height = arenaDim
    val position = vec2.zero
  }
  val arenaPadding = new scalene.components.RectangleShape {
    val width, height = arenaDim - 300
    val position = vec2.zero
  }

  this ++= swifts

  override def update() {

    grid.clear()

    for(swift <- swifts) {
      if( !arena.hitTest(swift.position) ) swift.position = swift.position.unit * arenaDim/2.1f
      val c = grid.at(swift.position)
      c.swifts += swift
    }

    val swiftbuf = ListBuffer[Swift]()
    val posbuf = ListBuffer[vec2]()
    val velbuf = ListBuffer[vec2]()
    val lookaround = 1

    for {
      x <- lookaround until grid.xcells - lookaround
      y <- lookaround until grid.ycells - lookaround
      c = grid.cell(x,y)
      if(c.swifts.length > 0)
      _ = swiftbuf.clear()
      _ = {
        for {
          xx <- x - lookaround to x + lookaround
          yy <- y - lookaround to y + lookaround
        } {
          swiftbuf ++= c.swifts
        }
      }
      avgPos = swiftbuf.map(_.position).reduce(_+_) / swiftbuf.length
      avgVel = swiftbuf.map(_.velocity).reduce(_+_) / swiftbuf.length
      count = swiftbuf.size
      swift <- swiftbuf
    } {
      for(other <- swiftbuf if other != swift) {
        swift.acceleration += (swift.position - other.position) / 100
      }
      if(!arenaPadding.hitTest(swift.position)) {
        swift.acceleration += -swift.position / 10
      }
      if(count > 1)
        swift.velocity += (swift.position - avgPos).unit
    }
    super.update()
  }

  override def render() {
    draw.fill(true)
    Color(0x777799).bind()
    val d = grid.xDim

    for(x <- 0 until grid.xcells; y <- 0 until grid.ycells) {
      draw.rect( grid.bottomLeft + vec(x,y)*grid.xDim, 2, 2 )
    }
    for(x <- 0 to grid.xcells) {
      draw.line( grid.bottomLeft + vec(x*d, 0), grid.bottomLeft + vec(x*d, grid.ycells*d) )
    }
    for(y <- 0 to grid.ycells) {
      draw.line( grid.bottomLeft + vec(0, y*d), grid.bottomLeft + vec(grid.xcells*d, y*d) )
    }
    Color.black.bind()
    super.render()

    Color(0xff7799).bind()
    draw.fill(false)
    arena.render()
    arenaPadding.render()
  }
}
