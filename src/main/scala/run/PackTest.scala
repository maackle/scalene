package run

import java.io.File
import scalene.gfx.Bitmap
import maackle.util._
import scalene.core.ScaleneApp
import scalene.traits.State
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

object Packer {

  def filesAt(f: File): Array[File] = if (f.isDirectory) {
    f.listFiles flatMap filesAt
  }
  else {
    Array(f)
  }

  def apply(paths: String*) = {
    val files = paths
      .map (path => new File(path))
      .map (f => {assert(f.exists()); f})
      .flatMap (filesAt)

    val images = files
      .filterNot(_.getPath.contains(".DS_Store")) //TODO: filter all invisible files/dirs
      .map {
        f => ImageIO.read(f)
      }
      .sortBy(-_.getHeight)

    val maxw = (0 /: images)(_ + _.getWidth)
    val maxh = (0 /: images)(_ + _.getHeight)
    val buf = new BufferedImage(maxw,maxh,BufferedImage.TYPE_INT_ARGB)
    var x = 0
    val g = buf.getGraphics
    for {
      im <- images
    } {
      g.drawImage(im, x, 0, null)
      x += im.getWidth
    }
    ImageIO.write(buf, "png", new File("out.png"))

  }
}

object PackTest extends App {
  val ts = Packer {
    "src/main/resources/img/packtest"
  }

}
//
//object PackTest extends ScaleneApp {
//
//  val windowSize = Some(600,600)
//  val windowTitle = "SCALENE GAME"
//  lazy val startState = new PackTestDomain.Do
//
//}
