package scalene.gfx

import scala.Array
import scalene.vector.vec
import scalene.core.VBO
import org.lwjgl.opengl.GL11._

case class ClipRect(x:Int, y:Int, w:Int, h:Int)

trait SubTexture extends Textured {
  //  protected def texture:Texture
  def clip:ClipRect

  lazy protected val (texWidth, texHeight) = (tex.getImageWidth, tex.getImageHeight)
  lazy protected val (paddedWidth, paddedHeight) = (tex.getTextureWidth.toFloat, tex.getTextureHeight.toFloat)
  lazy protected val (tw, th) = (clip.w / paddedWidth, clip.h / paddedHeight )
  lazy private val (tx0, ty0) = (clip.x / paddedWidth, clip.y / paddedHeight)
  lazy private val (tx1, ty1) = (tx0 + tw, ty0 + th)

  lazy val texCoords = Array(
    vec(tx0, ty0),
    vec(tx1, ty0),
    vec(tx1, ty1),
    vec(tx0, ty1)
  ).reverse
  lazy val vertices = Array(
    vec(0, 0),
    vec(texWidth, 0),
    vec(texWidth, texHeight),
    vec(0, texHeight)
  )

  def blit(color:Color = Color.white) {
    color.bind()
    gl.fill(true)
    bindAnd {
//      vbo.draw(GL_TRIANGLE_FAN)
      oldWay
    }
    def oldWay = {
      gl.begin(GL_TRIANGLE_FAN) {
        glTexCoord2f(tx0, ty1)
        glVertex2f(0, 0)

        glTexCoord2f(tx1, ty1)
        glVertex2f(texWidth, 0)

        glTexCoord2f(tx1, ty0)
        glVertex2f(texWidth, texHeight)

        glTexCoord2f(tx0, ty0)
        glVertex2f(0, texHeight)
      }
    }
  }
}
