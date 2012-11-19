package run.packer

import java.io.File
import java.awt.image.BufferedImage
import grizzled.slf4j.Logger
import javax.imageio.ImageIO

object Packer {

  private def filesAt(f: File): Array[File] = if (f.isDirectory) {
    f.listFiles flatMap filesAt
  }
  else {
    Array(f)
  }




  private def packImages(atlasWidth:Int, atlasHeight: Int)
                        (images:Seq[BufferedImage])
                        (implicit pp:PackingPlan){

    val truths = images.map(pp.addImage(_))
    if(truths.forall(b => b))
      pp.createAtlas()
    else {
      Logger("pack").info(truths)
      Logger("pack").info("problem!")
    }
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

    val maxw = (0 /: images)(_ + _.getWidth) / 2
    val maxh = (0 /: images)(_ + _.getHeight) / 1

    packImages(maxw, maxh)(images)(new BadPlan)

  }
}

trait PackingPlan {
  def addImage(im:BufferedImage):Boolean
  def createAtlas()
}