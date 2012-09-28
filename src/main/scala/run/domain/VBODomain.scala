package run.domain

import scalene.traits.{Layer, View2D, ViewSingle2D, State}
import run.Run
import org.lwjgl.BufferUtils
import org.lwjgl.opengl._
import scalene.vector.vec
import java.nio.DoubleBuffer
import scalene.core.DrawOp
import scalene.gfx.gl
import grizzled.slf4j.Logging

object VBODomain { domain =>

  class VBOTestState extends State(Run) with Logging {

    VBO.init()

    val drawing = DrawOp {
      VBO.draw()
    }
    this += drawing

    object VBO {
      import GL11._
      import GL15._

      val N = 8

      var vbuf_id = 0
      var ixbuf_id = 0

      def draw() {
        gl.fill(false)
        glClearColor(0.5f, 0.5f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, vbuf_id);
        glVertexPointer(2, GL_DOUBLE, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ixbuf_id);

        glDrawElements(GL_LINE_LOOP, N, GL_UNSIGNED_INT, 0);

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);

      }
      def init() {

        if(!GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
          throw new Exception("get a new video card, sucka!  bwaaahaha!")
        }

        vbuf_id = GL15.glGenBuffers()
        ixbuf_id = GL15.glGenBuffers()

        val vbuf = BufferUtils.createDoubleBuffer(N * 2)
        val vecs = for(i <- Array.range(0,N)) yield (vec.polar(1, i * 2*math.Pi / N))
        val vs = vecs flatMap { v => Seq(v.x.toDouble, v.y.toDouble)}
        vs foreach (println(_))
        vbuf.put(vs)
        vbuf.flip()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbuf_id)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbuf, GL15.GL_STATIC_DRAW)

        val ixbuf = BufferUtils.createIntBuffer(N)
        ixbuf.put(Array.range(0, N))
        ixbuf.flip()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ixbuf_id)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ixbuf, GL15.GL_STATIC_DRAW)

        GL15.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE);
      }
    }

    val views = new ViewSingle2D {
      val view = new View2D {
        zoom = 10
        val layers = Vector( new Layer2D(1)(drawing :: Nil) )
      }
    }
  }

}
