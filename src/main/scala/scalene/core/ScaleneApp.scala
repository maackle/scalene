package scalene.core

import scalene.common._
import scalene.gfx.{TTF, Color, gl, GLSettings}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl._
import grizzled.slf4j.{Logger, Logging}
import scalene.input.LWJGLKeyboard
import scalene.event.{MouseEventSource, Event, KeyEventSource, EventSource}
import scalene.helpers.MemDouble
import traits.Initialize
import scalene.common
import scalene.vector.vec
import scalene.audio.SoundStore
import org.lwjgl.opengl


trait ScaleneAppDebug extends ScaleneApp {

  lazy val font = TTF.default

  def avgFPS:Double
  def debugColor:Color
  def extraDebugText:String = ""

  override def loopBody() {
    super.loopBody()
    GLSettings.viewHUD()
    font.drawString(
      "fps: %d (%4.1f ms)\n%s".format(avgFPS.round.toInt, avgExecutionTime, extraDebugText),
      vec(5, currentWindowSize._2 - 5),
      debugColor,
      vec(-1,-1)
    )
  }
}

abstract class ScaleneApp
extends App /*with ScaleneInnerClasses*/
with Initialize {

  val windowSize:Option[(Int,Int)]
  val windowTitle:String
  val startState:StateBase
  def currentState:StateBase = stateMachine.current
  val fps = 60
  val dt = 1 / fps.toFloat
  val vsync = true
  lazy val msecsStartup = milliseconds
  def fullscreen = windowSize.isEmpty

  protected[scalene] lazy val stateMachine = new StateMachine(startState)
  private var _tick = 0
  private var _loopTime, _executionTime = MemDouble(32)
  private var _startupTime = 0.0

  private def _winsize = windowSize.get

  def currentWindowSize = {
    (Display.getWidth, Display.getHeight)
  }

  private val _eventSources = collection.mutable.Set[EventSource[Event]](
    new KeyEventSource,
    new MouseEventSource
  //TODO: add mouse source, controllers, etc.
  )
  def eventSources = _eventSources.toSeq

  def avgExecutionTime = _executionTime.avg
  def avgFPS = {
    1000 / _loopTime.avg
  }
  def lastFPS = {
    1000 / _loopTime.now
  }
  def ticks = _tick
  def millis:Int = (common.milliseconds - _startupTime).toInt

  delayedInit {
    Logger("ScaleneApp").info("starting up at %s" format msecsStartup)
    run()
  }

  def run() {
    initialize()
    var _ms = milliseconds
    while(!org.lwjgl.input.Keyboard.isKeyDown(LWJGLKeyboard.KEY_ESCAPE)) {
      val loopStartTime = milliseconds
      GLSettings.orthographic()
      loopBody()
      _tick += 1
      _loopTime << (milliseconds - _ms)
      _executionTime << (milliseconds - loopStartTime)
      _ms = loopStartTime
      //      TextureImpl.bindNone() // TODO: this may be necessary if font rendering glitches out...
      Display.update()
      Display.sync(fps)
    }
    cleanup()
  }

  def initialize() {
    Display.setTitle(windowTitle)
    Display.setVSyncEnabled(vsync)
    if(fullscreen) Display.setFullscreen(true)
    else Display.setDisplayMode(new DisplayMode(_winsize._1, _winsize._2))
    val pxfmt = new PixelFormat().withDepthBits(24).withSRGB(true)
    val ctxAttr = new ContextAttribs(3, 0)//.withForwardCompatible(true);
//    ctxAttr.withProfileCore(true);
    Display.create(pxfmt, ctxAttr)
    Logger("ScaleneApp").info("Display created")
    Logger("ScaleneApp").info("bpp: " + Display.getDisplayMode.getBitsPerPixel)

    // as soon as the display is initialized we can load resources
    Resource.beginAutoloading()

    SoundStore.init()

    GLSettings.defaults()
    GLSettings.orthographic()

  }

  def cleanup() {
    Display.destroy()
  }

  def loopBody() {
    currentState.__update(dt)
    _eventSources.foreach(_.__update(dt))

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    gl.matrix {
      currentState.__render()
    }

  }

}
