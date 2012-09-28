package scalene.gfx

import org.newdawn.slick.opengl.{Texture, TextureLoader}
import maackle.util._
import org.lwjgl.opengl.GL11._
import scalene._
import vector._
import grizzled.slf4j.Logging
import org.lwjgl.opengl.GL11
import java.io.InputStream

class Bitmap(val path:String, var offset:vec2=null) extends Textured with Logging {
  protected val texture = loadTexture(path)
  val (iw, ih) = (texture.getImageWidth.toFloat, texture.getImageHeight.toFloat)
  val (rx, ry) = (iw / texture.getTextureWidth, ih / texture.getTextureHeight )
  if(offset==null)
    offset = vec(iw/2, ih/2)
  val width = iw
  val height = ih

  def blit(color:Color = Color.white) {
    color.bind()
    gl.fill(true)
    bind {
      glBegin(GL_QUADS)
      glTexCoord2f(0, 0)
      glVertex2f(0, ih)
      glTexCoord2f(rx, 0)
      glVertex2f(iw, ih)
      glTexCoord2f(rx, ry)
      glVertex2f(iw, 0)
      glTexCoord2f(0, ry)
      glVertex2f(0, 0)
      glEnd()
    }
  }

  private def loadTexture(path:String) = {
    val reg = """.*\.(.+?)$""".r
    val reg(ext) = path
    val tex = ext match {
      case "png" | "gif" | "jpg" | "jpeg" => { TextureLoader.getTexture(ext, getStream(path)) }
      case _ => throw new Exception("image format '%s' is not recognized".format(ext))
    }
    tex
  }

  override def toString = "Bitmap(\"%s\")(%dx%d)".format(path, iw, ih)
}

trait Textured {
  protected def texture:Texture
  def bound = Textured.currentTexture == this

  def bind(andThenDo: =>Unit) {
    //      glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE)
    if(!bound) {
      texture.bind()
      Textured.currentTexture = this
    }
    require(Textured.bind_lock==0, "nested bind blocks in Textured instance")
    Textured.bind_lock += 1
    gl.texture2(andThenDo)
    Textured.bind_lock -= 1
  }
}
object Textured {
  private var bind_lock = 0
  var currentTexture:Textured = null
}

