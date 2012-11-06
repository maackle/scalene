package scalene.core

import scalene.common._
import scalene.gfx.{GLSettings, Image, gl, Color}
import org.lwjgl.BufferUtils
import java.nio.{IntBuffer, FloatBuffer}
import scalene.vector._
import scalene.traits._
import maackle.util.getStream
import org.lwjgl.opengl.GL11._
import org.lwjgl.util.glu.GLU
import org.lwjgl.opengl._
import org.newdawn.slick.opengl.{TextureImpl, TextureLoader, Texture}
import grizzled.slf4j.Logging
import scalene.gfx.GLSettings
import scalene.input.LWJGLKeyboard
import scalene.event.{Event, KeyEventSource, EventSource}
import scalene.helpers.{MemDouble, MemInt}
import scalene.traits.State.StateMachine
import scalene.helpers.MemDouble

//trait ScaleneInnerClasses { app:ScaleneApp => }

abstract class ScaleneApp
extends App /*with ScaleneInnerClasses*/
with Initialize
with Logging {

  val windowSize:Option[(Int,Int)]
  val windowTitle:String
  val startState:State
  def currentState:State = stateMachine.current
  val fps = 60
  val vsync = true
  lazy val msecsStartup = milliseconds
  def fullscreen = windowSize.isEmpty

  protected lazy val stateMachine = new StateMachine(startState)
  private var _tick = 0
  private var _loopTime = MemDouble(32)

  private def _winsize = windowSize.get

  private val _eventSources = collection.mutable.Set[EventSource[Event]](
    new KeyEventSource
  //TODO: add mouse source, controllers, etc.
  )
  def eventSources = _eventSources.toSeq

  //TODO: let's not use this for the basics.  only for user-created Sources (none exist yet (10-8))
//  private def registerEventSource(es:EventSource) {
//    _eventSources += es
//  }

  def avgFPS = {
    1000 / _loopTime.avg
  }
  def lastFPS = {
    1000 / _loopTime.now
  }
  def ticks = _tick

  delayedInit {

    info("starting up at %s" format msecsStartup)
    doInitialize()
    var _ms = milliseconds
    while(!org.lwjgl.input.Keyboard.isKeyDown(LWJGLKeyboard.KEY_ESCAPE)) {
      loopBody()
      _tick += 1
      val newms = milliseconds
      _loopTime << (milliseconds - _ms)
      _ms = newms
//      TextureImpl.bindNone() // TODO: find a way to remove this!
      if(_tick % 100 == 0) println("FPS: %s %s" format (avgFPS, lastFPS))
      Display.sync(fps)
    }
    doCleanup()
  }

  initialize {
    Display.setTitle(windowTitle)
    Display.setVSyncEnabled(vsync)
    if(fullscreen) Display.setFullscreen(true)
    else Display.setDisplayMode(new DisplayMode(_winsize._1, _winsize._2))
    val pxfmt = new PixelFormat()
    val ctxAttr = new ContextAttribs(3, 0)
    ctxAttr.withForwardCompatible(true);
//    contextAtrributes.withProfileCore(true);
    Display.create(pxfmt, ctxAttr)
    info("Display created")
    info("bpp: " + Display.getDisplayMode.getBitsPerPixel)

    Resource.startAutoload(true) // as soon as the display is initialized we can load resources

    GLSettings.defaults()
    GLSettings.orthographic()

  }

  cleanup {
    Display.destroy()
  }

  def loopBody() {
    currentState.update()
    _eventSources.foreach(_.update())

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    gl.matrix {
      currentState.__render()
    }

    Display.update()
  }

}
