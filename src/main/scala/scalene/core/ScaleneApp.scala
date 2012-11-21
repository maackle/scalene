package scalene.core

import scalene.common._
import scalene.gfx.{Color, gl, GLSettings}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl._
import grizzled.slf4j.Logging
import scalene.input.LWJGLKeyboard
import scalene.event.{Event, KeyEventSource, EventSource}
import scalene.helpers.MemDouble
import traits.Initialize
import scalene.core.State.StateMachine
import scalene.common

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
  private var _startupTime = 0.0

  private def _winsize = windowSize.get

  def currentWindowSize = {
    (Display.getWidth, Display.getHeight)
  }

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
  def millis:Int = (common.milliseconds - _startupTime).toInt

  delayedInit {

    info("starting up at %s" format msecsStartup)
    initialize()
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
    cleanup()
  }

  def initialize() {
    Display.setTitle(windowTitle)
    Display.setVSyncEnabled(vsync)
    if(fullscreen) Display.setFullscreen(true)
    else Display.setDisplayMode(new DisplayMode(_winsize._1, _winsize._2))
    val pxfmt = new PixelFormat().withDepthBits(24).withSamples(4).withSRGB(true)
    val ctxAttr = new ContextAttribs(3, 0)//.withForwardCompatible(true);
//    contextAtrributes.withProfileCore(true);
    Display.create(pxfmt, ctxAttr)
    info("Display created")
    info("bpp: " + Display.getDisplayMode.getBitsPerPixel)

    // as soon as the display is initialized we can load resources
    Resource.beginAutoloading()

    GLSettings.defaults()
    GLSettings.orthographic()

  }

  def cleanup() {
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
