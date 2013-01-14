package scalene.gfx

import java.io.{FileInputStream, File}
import org.newdawn.slick.opengl.TextureLoader
import maackle.util._
import scalene.core.Resource
import scalene.core.traits.Render


trait ImageLike extends SubTexture with Render{
  def clip:ClipRect
  def width:Int
  def height:Int
}

case class Image(val texResource:Resource[Tex], cliprect:ClipRect) extends ImageLike {

  lazy val clip = if(cliprect==null) ClipRect(0,0,texWidth,texHeight) else cliprect
  val (width, height) = (clip.w, clip.h)
  def render() { blit() }
  override def toString = "Image(tex=%s, clip=%s)".format(tex, clip)
}

object Image {

  def apply(path:String, clip:ClipRect = null) = {
    new Image(Resource(path)(Tex.load), clip)
  }

}

