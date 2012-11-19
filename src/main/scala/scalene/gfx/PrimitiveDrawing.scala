package scalene.gfx

import org.jbox2d.common.Vec2
import org.lwjgl.opengl.GL11._
import math._
import scalene._
import scalene.vector._

trait PrimitiveDrawing extends gl {
  type R = common.Real
  type Vertex = (V,V)
  type VertexList = Array[Vertex]
  type VecList = Array[Vec2]

  protected var circlepts:Map[ Int, VertexList ] = Map()

  @inline
  def bindVertices(verts:Seq[vec2]) {
    for(v <- verts) vertex(v)
  }
  @inline
  def bindVertices(verts:VertexList) {
    for(v <- verts) { vertex(v._1, v._2)

    }
  }
//  @inline
//  def bindVertices(verts:VecList) {
//    for(v <- verts) vertex(v)
//  }

  @inline
  protected def getCircle(num:Int):VertexList = {
    if (!circlepts.isDefinedAt(num)) {
      circlepts += num -> {
        for (i:Int <- Array.range(0, num)) yield ( cos(2.0*Pi*i/num) , sin(2.0*Pi*i/num) )
      }
    }
    circlepts(num)
  }

  @inline def unitCircle(num:Int=100) {
    if (!circlepts.isDefinedAt(num)) {
      circlepts += num -> {
        for (i:Int <- Array.range(0, num)) yield  ( cos(2.0*Pi*i/num) , sin(2.0*Pi*i/num) )
      }
    }
    begin(GL_POLYGON) {
      bindVertices(circlepts(num))
    }
  }


  @inline def circle(radius:R, center:vec2=null, num:Int=0) {
    @inline def guessCircleNum(radius:R) = 16

    glPushMatrix()
    if(center!=null) translate(center)
    scale(radius, radius)
    unitCircle(if(num>0) num else guessCircleNum(radius))
    glPopMatrix()
  }

  def points(points: VertexList) {
    glBegin(GL_POINTS)
    for (v <- points) vertex(v._1, v._2)
    glEnd()
  }
  def line(pair:(vec2, vec2)) {
    glBegin(GL_LINES)
    vertex(pair._1)
    vertex(pair._2)
    glEnd()
  }
  def line(v1:vec2, v2:vec2) {
    glBegin(GL_LINES)
    vertex(v1)
    vertex(v2)
    glEnd()
  }

  def vector(origin:vec2, to:vec2) {
    line(origin, origin+to)
  }
  def vector(pair:(vec2,vec2)) {
    line((pair._1, pair._1 + pair._2))
  }
  def rect(pos:vec2, w:common.Real, h:common.Real) {
    gl.fill(true)
    gl.begin(GL_POLYGON) {
      gl.vertex(pos)
      gl.vertex(pos + vec(0, h))
      gl.vertex(pos + vec(w, h))
      gl.vertex(pos + vec(w, 0))
    }
  }
}

object draw extends  PrimitiveDrawing