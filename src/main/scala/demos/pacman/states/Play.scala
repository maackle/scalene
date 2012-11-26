package demos.pacman.states

import scalene.core.{View2D, State}
import demos.pacman.{Pacman => Game}
import scalene.gfx.Color
import demos.pacman.things.Pacman
import scalene.vector.vec

class Play extends State(Game) {

  this += new Pacman(vec(0,0))

  val view = View2D.simple(Color.gray, renderables.toSeq)
}
