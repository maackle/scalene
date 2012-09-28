package scalene.core

import scalene.common._
import scalene.gfx.{GLSettings, Bitmap, gl, Color}
import org.lwjgl.BufferUtils
import java.nio.{IntBuffer, FloatBuffer}
import scalene.vector._
import scalene.traits._
import maackle.util.getStream
import org.lwjgl.opengl.GL11._
import org.lwjgl.util.glu.GLU
import org.lwjgl.opengl.{GL11, PixelFormat, DisplayMode, Display}
import org.newdawn.slick.opengl.{TextureLoader, Texture}
import grizzled.slf4j.Logging
import scalene.gfx.GLSettings
import scalene.input.LWJGLKeyboard
import scalene.components.EventSource
import scalene.helpers.{MemDouble, MemInt}

abstract class ScaleneApp extends App with Initialize with Logging {

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

  private val _eventSources = collection.mutable.Set[EventSource]()
  def eventSources = _eventSources.toSeq
  def registerEventSource(es:EventSource) {
    _eventSources += es
  }

  def avgFPS = {
    1000 / _loopTime.avg
  }
  def lastFPS = {
    1000 / _loopTime.now
  }

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
      if(_tick % 100 == 0) println("FPS: %s %s" format (avgFPS, lastFPS))
    }
    doCleanup()
  }

  initialize {
    Display.setTitle(windowTitle)
    Display.setVSyncEnabled(vsync)
    if(fullscreen) Display.setFullscreen(true)
    else Display.setDisplayMode(new DisplayMode(_winsize._1, _winsize._2))
    Display.create(new PixelFormat(8, 16, 4))
    Display.sync(fps)
    info("Display created")

    Resource.loadAll()

    GLSettings.defaults()

    GLSettings.orthographic()

  }

  cleanup {
    Display.destroy()
  }

  def loopBody() {
    currentState.update()

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    gl.matrix {
      currentState.__render()
    }

    Display.update()
  }

}
