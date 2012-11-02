package scalene.gfx


import scalene.misc.Java
import maackle.util._
import org.newdawn.slick.{UnicodeFont}
import org.newdawn.slick.font.effects.ColorEffect
import org.lwjgl.opengl.GL11._

import scalene.vector.{vec, vec2}
import scalene.core.Resource
import grizzled.slf4j.Logging
import java.io.File
import java.awt

class TTF(path:String, size:Int, style:TTF.FontStyle.Plain.type) {

  var anchor = vec2.zero

  //TODO: resource-ify this
  lazy val uni = {
    val u = new UnicodeFont(path, size, false, false)
//    val u = new UnicodeFont(f)
    u.addNeheGlyphs()
  //   uni.addGlyphs(0x21ba, 0x21bb)
    Java.addEffect(u, (new ColorEffect(java.awt.Color.WHITE)))
    u.loadGlyphs()
    u
  }
  //TODO: offsets
  def drawString(what:String, pos:vec2, color:Color, anchor:vec2=anchor, scale:Float=1f) {
    gl.fill(true)
    val w = uni.getWidth(what)
    val h = uni.getHeight(what)
    val x = pos.x - (anchor.x/2 + .5f) * w
    val y = pos.y - (anchor.y/2 + .5f) * h
    val offset = vec(
      (anchor.x/2 + .5f) * -w,
      (anchor.y/2 + .5f) * h
    )
    gl.matrix {
      gl.translate(pos)
      gl.scale(scale, scale)
      gl.translate(offset)
      gl.scale(1,-1)
      uni.drawString(0, 0, what, color.toSlick )
    }
  }

}

object TTF extends Logging {
//  def load(path:String, size:Int) = {
//    val font =
//      try {
//        java.awt.Font.createFont(Font.TRUETYPE_FONT, getStream(path) )
//      }
//      catch {
//        case e:java.lang.RuntimeException => new Font(path, Font.PLAIN, size)
//      }
//    info("loaded font: " + font)
//    font
//  }

  def load(path:String, size:Float) = {
    val base = awt.Font.createFont(awt.Font.TRUETYPE_FONT, getStream(path))
    val font = base.deriveFont(awt.Font.PLAIN, size)
    info("loaded font: " + font)
    font
  }

  def apply(path:String, size:Int, style:FontStyle.Plain.type=FontStyle.Plain):TTF = {
//    val font = Resource(path) ( load(_,size) )
    new TTF(path, size, style)
  }


  object FontStyle extends Enumeration {
    val Plain, Bold, Italic = Value
    val BoldItalic = Value(4)
  }
}