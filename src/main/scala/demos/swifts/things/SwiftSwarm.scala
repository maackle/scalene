package demos.swifts.things

import scalene.gfx._
import org.lwjgl.opengl.GL11
import scalene.vector.{vec2, vec}
import scalene.core.{VBO, ThingStore}
import demos.swifts.TheSwifts
import scalene.common
import scalene.misc.ScaleneGrid
import collection.mutable.{ArrayBuffer, ListBuffer}
import com.tinyline.tiny2d.i
import scalene.components.{Rotation, Acceleration2D, RectangleShape}
import scalene.core.traits.Render

class GridCell {
  //    val velocity = vec2.zero
  val swifts = ListBuffer[Swift]()
  val neighbors = ListBuffer[Swift]()

  var avgPosition, avgVelocity = vec2.zero

  def clear() {
    swifts.clear()
    neighbors.clear()
    avgVelocity = vec2.zero
    avgPosition = vec2.zero
  }
}

class SwiftSwarm(size:Int, hawk:Hawk) extends TriangleBatch {

  val vertexCapacity = size * 3

  val numGridDivisions = 50
  val arenaDim = 2000f

  val arenaColor = Color(0x7ebcbb)

  val vbo = VBO.create(vertexCapacity, false, false, false)

  def app = TheSwifts

  val grid = ScaleneGrid.square(arenaDim, numGridDivisions, vec(0,0))(new GridCell)

  def shape = {
    val verts = 3
    val sz = 4
    val r = scala.util.Random.nextFloat() * 0.5f
    (for (t <- List(0.0f, 0.8f + r, 2.2f - r)) yield {
      vec.polar(sz, t * (2*math.Pi) / verts)
    }).toArray
  }

  val swifts = for (i <- 1 to size) yield {
    new Swift(vec.polar.random(500, 0, math.Pi))
  }

  val arena = new scalene.components.RectangleShape with Render {
    val width, height = arenaDim
    val position = vec2.zero
    def render = draw()
  }

  val arenaPadding = new scalene.components.RectangleShape with Render {
    val width, height = arenaDim - 300
    val position = vec2.zero
    def render = draw()
  }

  this ++= swifts

  override def update(dt:Float) {

    grid.clear()

    for(swift <- swifts) {
      if( !arena.hitTest(swift.position) ) swift.position = swift.position.unit * arenaDim/2.1f
      val c = grid.at(swift.position)
      c.swifts += swift
      swift.cell = c
    }

    val swiftbuf = ListBuffer[Swift]()
    val posbuf = ListBuffer[vec2]()
    val velbuf = ListBuffer[vec2]()
    val lookaround = 1

    val nonEmptyCells = ArrayBuffer[GridCell]()

    for {
      x <- lookaround until grid.xcells - lookaround
      y <- lookaround until grid.ycells - lookaround
      c = grid.cell(x,y)
      xx <- x - lookaround to x + lookaround
      yy <- y - lookaround to y + lookaround
    } {
      c.neighbors ++= c.swifts
    }

    for {
      c <- grid.cells
      num = c.neighbors.length
      if(num > 0)
    } {
      for {
        swift <- c.neighbors
        p = swift.position
        v = swift.velocity
      } {
        c.avgPosition += p
        c.avgVelocity += v
      }
      c.avgPosition /= num
      c.avgVelocity /= num
    }

    for(swift <- swifts) swift.updateFeltForces(hawk, arenaPadding)

    super.update(dt)
  }

  def drawGrid() {

    Color(0xaaaadd).bind()
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
  }

  override def render() {

    draw.fill(true)

    arenaColor.bind()
    arena.render()

//    drawGrid()

    Color(0x333333).bind()
    super.render()

    Color(0xff7799).bind()
    draw.fill(false)
    arenaPadding.render()
  }
}
