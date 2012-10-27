package scalene.gfx

import scalene.vector._
import scalene.traits.{InternalTransform, Render, Thing}
import scalene.core.Resource
import org.newdawn.slick.opengl.Texture
import java.io.File
import org.lwjgl.opengl.GL11._

case class ClipRect(x:Int, y:Int, w:Int, h:Int)

trait SubTexture extends Textured {
//  protected def texture:Texture
  def clip:ClipRect

  lazy val (width, height) = (tex.getImageWidth, tex.getImageHeight)
  lazy private val (texWidth, texHeight) = (tex.getTextureWidth.toFloat, tex.getTextureHeight.toFloat)
  lazy private val (tw, th) = (clip.w / texWidth, clip.h / texHeight )
  lazy private val (tx0, ty0) = (clip.x / texWidth, clip.y / texHeight)
  lazy private val (tx1, ty1) = (tx0 + tw, ty0 + th)

  lazy val printem = {
    println(tx0, ty0, tx1, ty1)
  }

  def blit(color:Color = Color.white) {
    color.bind()
    gl.fill(true)
    bindAnd {
      glBegin(GL_QUADS)
      glTexCoord2f(tx0, ty1)
      glVertex2f(0, height)

      glTexCoord2f(tx1, ty1)
      glVertex2f(width, height)

      glTexCoord2f(tx1, ty0)
      glVertex2f(width, 0)

      glTexCoord2f(tx0, ty0)
      glVertex2f(0, 0)
      glEnd()
    }

  }

  protected def loadTextureFromString = Bitmap.load(_:String)
  protected def loadTextureFile = Bitmap.load(_:File)
}

class Sprite(path:String, center:vec2 = null, cliprect:ClipRect = null)
extends Thing with SubTexture with InternalTransform with Render {
  protected val bmp = Resource(path)(loadTextureFromString)
  def tex = bmp.is
  lazy val offset = if(center==null) vec(-width/2, -height/2) else -center
  lazy val clip = if(cliprect==null) ClipRect(0,0,width,height) else cliprect
  lazy val __transform = Transform.static(translate = offset)
  def render() {
    blit()
  }
}