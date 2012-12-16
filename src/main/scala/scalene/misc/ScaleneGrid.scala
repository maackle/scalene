package scalene.misc

import scalene.common._
import scalene.vector.{vec, vec2}


trait ScaleneGrid[T <: { def clear():Unit } ] {

  type I = Real

  protected val g:Array[T]
  val xDim, yDim:I

//  val width, height : I
  val xcells, ycells : Int

  val bottomLeft:vec2

  def at(v:vec2):T = {
    at(v.x, v.y)
  }

  private def at(x:I, y:I):T = {
    val (x_, y_) = (x - bottomLeft.x, y - bottomLeft.y)
    cell((x_ / xDim).toInt, (y_ / yDim).toInt)
  }

  def cell(x:Int, y:Int):T = {
    g(y*xcells + x)
  }

  def clear() {
    g.foreach(_.clear())
  }

  def cells:Seq[T] = g

}

object ScaleneGrid {

  def square[T <: { def clear():Unit } : ClassManifest](length:Real, divisions:Int, center:vec2)(init: =>T) = {

    new ScaleneGrid[T] {
      val xcells, ycells = divisions
      val xDim, yDim = length/divisions
      val bottomLeft = center - vec2.one * (length/2)
      val g = Array.fill(xcells * ycells)(init)

      println(xDim,yDim)
      println(xcells,ycells)
      println(bottomLeft)
    }
  }

}
