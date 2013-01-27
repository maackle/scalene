package demos.dolphin.states

import scalene.core.{Resource, View2D, State}
import scalene.gfx.{Image, SpriteSheet, Color}
import scalene.vector.vec
import scalene.event.{KeyDown, EventHandler, EventSink}
import scalene.misc.Easing
import demos.dolphin.Dolphin
import demos.dolphin.things.Dolphin
import demos.dolphin.Dolphin

class Play extends State(Dolphin)  {

  val dolphin = new Dolphin

  this += dolphin
  val view = View2D.simple(Color.gray, renderables)

}
