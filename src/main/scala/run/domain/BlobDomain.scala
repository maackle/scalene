package run.domain

import scalene.traits._
import run.Run
import scalene.event.KeyEventSource
import scalene.components.{Position2D, Position}
import scalene.gfx._
import scalene.vector.{vec2, vec}
import scalene.misc.SolidBackground


object BlobDomain extends Domain2D(Run) { domain =>

  class Blob(val position:vec2) extends Thing with Position2D with Update with Render with InternalTransform {

    val __transform = Transform.dynamic(()=>position, null, null)
    val color = Color.magenta * maackle.util.Random.uniform(0.5,1).toFloat

    def update() {
      position.x += 0.05
    }

    def render() {
      color.bind()
      draw.circle(1.0)
    }

  }

  object PlayState extends State(domain) {
    val N = 2000
    val n = math.sqrt(N).toInt
    val transform = Transformer2D.identity

    val blobs = for {
      i <- -n/2 to n/2
      j <- -n/2 to n/2
    } yield new Blob(vec(i*20, j*20))

    this ++= blobs

    val view = new ViewSingle2D {

      val view = new View2D {
        zoom = 2
        val layers = Vector(
          new Layer2D(0)(new SolidBackground(Color.gray) :: Nil),
          new Layer2D(1)(blobs),
          new Layer2D(0.8)(blobs),
          new Layer2D(0.6)(blobs)
        )

      }

    }

  }

}
