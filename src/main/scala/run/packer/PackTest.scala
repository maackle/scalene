package run.packer

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color


object PackTest extends App {

  val ts = Packer {
    "src/main/resources/img/packtest"
  }

  def generateTestImages() {
    def randomImage(w:Int, h:Int) = {
      val buf = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB)
      val g = buf.getGraphics
      g.setColor(Color.white)
      g.drawRect(0,0,w,h)
      g.setColor(Color.getHSBColor(math.random.toFloat, 1, 1))
      g.fillRect(1,1, w-2, h-2)
      buf
    }
    def randDim = math.abs(util.Random.nextInt % 100) + 16
    for(i <- 1 to 100) {
      ImageIO.write(
        randomImage(randDim, randDim),
        "png",
        new File("src/main/resources/img/packtest/%s.png".format(i))
      )
    }
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


