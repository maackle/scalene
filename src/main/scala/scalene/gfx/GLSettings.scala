package scalene.gfx

import org.lwjgl.opengl.GL11._
import grizzled.slf4j.Logging

object GLSettings extends Logging {

  def defaults() {
    glDisable(GL_DEPTH_TEST)
    glDisable(GL_LIGHTING)

    glEnable (GL_BLEND)
    glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    info("set up default OpenGL settings")

  }

  def orthographic() {
    import org.lwjgl.opengl.Display
    val width = Display.getDisplayMode.getWidth
    val height = Display.getDisplayMode.getHeight

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(-width/2, width/2, -height/2, height/2, -1, 1)
  }

  def viewHUD() {
    import org.lwjgl.opengl.Display
    val width = Display.getDisplayMode.getWidth
    val height = Display.getDisplayMode.getHeight

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, width, 0, height, -1, 1)
  }
}
