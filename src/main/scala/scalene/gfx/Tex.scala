package scalene.gfx

import org.newdawn.slick.opengl.{TextureLoader, TextureImpl, Texture}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13
import maackle.util._
import java.io.{FileInputStream, File}
import scalene.core.Resource

//class Tex(val id:Int, image:BufferedImage) {
//  val (getImageWidth, getImageHeight) = (image.getWidth, image.getHeight)
//  val (getTextureWidth, getTextureHeight) = (image.getWidth, image.getHeight)
//  def bind() {
//    glEnable(GL_TEXTURE_2D)
//    glBindTexture(GL_TEXTURE_2D, id)
//  }
//}

/**
 * Just a simple wrapper for Slick Texture
 *
 * @param t
 */
class Tex(t:Texture) {
  val (getImageWidth, getImageHeight) = (t.getImageWidth, t.getImageHeight)
  val (getTextureWidth, getTextureHeight) = (t.getTextureWidth, t.getTextureHeight)
  val id = t.getTextureID
  def bind() {
    if(Tex.lastBound != t) {
      glBindTexture(GL_TEXTURE_2D, t.getTextureID)
      Tex.lastBound = t
    }
  }
}

//TODO: don't rely on singleton!
object Tex {
  private var _lastBound:Texture = null
  def bindNone() { _lastBound = null }
  def lastBound = TextureImpl.getLastBind
  def lastBound_=(t:Texture) { TextureImpl.bindByForce(t) }


  def load(path:String) = {
    val reg = """.*\.(.+?)$""".r
    val reg(ext) = path
    val texture = ext match {
      case "png" | "gif" | "jpg" | "jpeg" => { TextureLoader.getTexture(ext, getStream(path)) }
      case _ => throw new Exception("image format '%s' is not recognized".format(ext))
    }
    new Tex(texture)
  }

  //TODO: test!
  def load(file:File) = {
    assert(file.isFile)
    val reg = """.*\.(.+?)$""".r
    val path = file.getPath
    val reg(ext) = path
    val texture = ext match {
      case "png" | "gif" | "jpg" | "jpeg" => { TextureLoader.getTexture(ext, new FileInputStream(file)) }
      case _ => throw new Exception("image format '%s' is not recognized".format(ext))
    }
    new Tex(texture)
  }

//  private var lastBoundId = -1
}


trait Textured {
  protected def texResource:Resource[Tex]
  def tex = texResource.is

  def bindAnd(andThenDo: =>Unit) {
//    glEnable(GL_TEXTURE_2D)
    GL13.glActiveTexture(GL13.GL_TEXTURE0)
    gl.texture2d {
      tex.bind()
      andThenDo
    }
  }
}




/**
  FAILURES:


import org.lwjgl.opengl.GL12._
import scalene._
import core.{ScaleneInnerClasses, ScaleneApp}
import vector._
import grizzled.slf4j.Logging
import org.lwjgl.opengl.GL11
import java.io.{FileInputStream, File, InputStream}
import java.nio.{IntBuffer, ByteOrder, Buffer, ByteBuffer}
import java.awt.image.{BufferedImage, DataBufferByte}
import org.lwjgl.BufferUtils
import java.awt.color.ColorSpace


  def load(file:File):Tex = load(ImageIO.read(file))
  def load(path:String):Tex = load(ImageIO.read(getStream(path)))

  class WrappedImage(im:BufferedImage) {
    val bpp = im.getColorModel.getNumComponents
    val fmt = bpp match {
      case 3 => GL_RGB
      case 4 => GL_RGBA
    }
    val (width, height) = (im.getWidth, im.getHeight)
    val (texWidth, texHeight) = {
      var tw, th = 1
      while(tw < width) tw*=2
      while(th < height) th*=2
      (tw, th)
    }
    println("dims: ", width, height, texWidth, texHeight)

    def writeBuffer() = {
      val data = im.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData
      val bi = new BufferedImage(width, height, bpp)
      val imsize = width * height * bpp
      val texsize = texWidth * texHeight * bpp
      val dirbuf = BufferUtils.createByteBuffer(imsize)
      assert(data.length == imsize)
      dirbuf.put(data)
      dirbuf.order(ByteOrder.nativeOrder())
      println(dirbuf.position(), dirbuf.limit(), dirbuf.remaining())
      dirbuf.flip()
      println(dirbuf.position(), dirbuf.limit(), dirbuf.remaining())

      glTexImage2D(GL_TEXTURE_2D, 0, fmt, width, height, 0, fmt, GL_UNSIGNED_BYTE, dirbuf)
    }

    def writePaddedBuffer() = {
      val data = im.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData
      val imsize = width * height * bpp
      val texsize = texWidth * texHeight * bpp
      val dirbuf = BufferUtils.createByteBuffer(texsize)
      assert(data.length == imsize)
      val zeros = Array.fill((texWidth - width)*bpp)(0.toByte)
      for {
        y <- 0 until height
      } {
        dirbuf.put(data, y*width*bpp, width*bpp)
        dirbuf.put(zeros)
      }
      dirbuf.put(Array.fill((texHeight - height)*width*bpp)(0.toByte))
      println(dirbuf.position(), dirbuf.limit(), dirbuf.remaining())
      dirbuf.flip()
      println(dirbuf.position(), dirbuf.limit(), dirbuf.remaining())
      dirbuf
      glTexImage2D(GL_TEXTURE_2D, 0, fmt, texWidth, texHeight, 0, fmt, GL_UNSIGNED_BYTE, dirbuf)
    }

  }

  def load(image:BufferedImage) = {
    val id = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, id)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    val wim = new WrappedImage(image)
    wim.writeBuffer()

    new Tex(id, image)
  }

  */