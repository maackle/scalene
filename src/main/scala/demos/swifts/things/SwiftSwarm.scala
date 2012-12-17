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

class SwiftSwarm(size:Int, val hawk:Hawk) extends TriangleBatch {

  val vertexCapacity = size * 3

  val numGridDivisions = 50
  val arenaDim = 2000f

  val arenaColor = Color(0x7ebcbb)

  val vbo = VBO.create(vertexCapacity, false, false, false)

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

  class Swift(var position:vec2) extends Birdy with Acceleration2D with Rotation with Render {

    var rotation = 0.0
    var velocity = position.rotate(math.Pi/2).unit * 200
    var acceleration = vec(0,0)

    val maxVelocity = 300

    val intentionFactor = maxVelocity // 50
    val confluenceFactor = 0.5f // 0.2
    val crowdingFactor = 0f
    val repulsionFactor = 30f
    val avoidanceFactor = hawk.maxVelocity * 4000
    val avoidancePower = 1.30

    var cell:GridCell = null


    def intention = {
      val c = intentionFactor
      val centerDesire = if(arenaPadding.hitTest(position)) c else c * 10
      -position / position.length * centerDesire
    }

    def confluence = cell.avgVelocity * confluenceFactor
    def crowding = (cell.avgPosition - position) * crowdingFactor

    var repulsion = vec2.zero

    def avoidance = {
      val diff = position - hawk.position
      val divisor = math.pow(diff.lengthSquared, avoidancePower).toFloat
      diff / divisor * avoidanceFactor
    }

    def repulse() {

      repulsion = vec2.zero

      for {
        other <- cell.neighbors if this != other
      } {
        val diff = (position - other.position)
        repulsion += diff / diff.lengthSquared
      }

      repulsion *= repulsionFactor
    }

    def simulate(dt:Float) {

      rotation = velocity.angle

      repulse()

      acceleration = (
        intention +
        repulsion +
        confluence +
        crowding +
        avoidance
      )
      velocity = velocity.limit(maxVelocity)

    }

    def render() {
      Color.green.bind()
      draw.vector(position, intention)
      Color.blue.bind()
      draw.vector(position, confluence)
      Color.red.bind()
      draw.vector(position, avoidance)
//      Color.cyan.bind()
//      draw.vector(position, crowding)
    }

  }

  def app = TheSwifts

  val grid = ScaleneGrid.square(arenaDim, numGridDivisions, vec(0,0))(new GridCell)

  val shape = {
    val verts = 3
    val sz = 4
    (for (t <- List(0.0f, 1.1f, 1.9f)) yield {
      vec.polar(sz, t * (2*math.Pi) / verts)
    }).toArray
  }

  val swifts = for (i <- 1 to size) yield {
    new Swift(vec.polar.random(500, 0, math.Pi))
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

    super.update()
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
