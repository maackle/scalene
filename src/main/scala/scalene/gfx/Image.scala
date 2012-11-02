package scalene.gfx

import java.io.{FileInputStream, File}
import org.newdawn.slick.opengl.TextureLoader
import maackle.util._
import scalene.vector.{vec, vec2}
import scalene.core.Resource


class Image(val tex:Tex, cliprect:ClipRect = null, centerPos:vec2 = null) extends SubTexture {
  lazy val clip = if(cliprect==null) ClipRect(0,0,texWidth,texWidth) else cliprect
  val (width, height) = (clip.w, clip.h)
  val center = if(centerPos==null) vec(width/2, width/2) else centerPos

  def render() {
    gl.matrix {
      gl.translate(-center)
      blit()
    }
  }
}

object Image {

  def resource(path:String) = Resource(path)(Image.load)
  def load(path:String) = new Image(new Tex(loadTexture(path)))
  def load(file:File) = new Image(new Tex(loadTexture(file)))

  private def loadTexture(path:String) = {
    val reg = """.*\.(.+?)$""".r
    val reg(ext) = path
    val tex = ext match {
      case "png" | "gif" | "jpg" | "jpeg" => { TextureLoader.getTexture(ext, getStream(path)) }
      case _ => throw new Exception("image format '%s' is not recognized".format(ext))
    }
    tex
  }

  //TODO: test!
  private def loadTexture(file:File) = {
    assert(file.isFile)
    val reg = """.*\.(.+?)$""".r
    val path = file.getPath
    val reg(ext) = path
    val tex = ext match {
      case "png" | "gif" | "jpg" | "jpeg" => { TextureLoader.getTexture(ext, new FileInputStream(file)) }
      case _ => throw new Exception("image format '%s' is not recognized".format(ext))
    }
    tex
  }
}

