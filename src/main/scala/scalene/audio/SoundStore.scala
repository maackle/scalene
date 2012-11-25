package scalene.audio

/** VERY INCOMPLETE, but working **/

/*
  TODO: general sound -- wave, midi, synth, user
  TODO: decide what the interface should be for grouping these, cloning, etc...
 */

import java.io.InputStream
import org.lwjgl.{BufferUtils, LWJGLException}
import org.lwjgl.openal.{AL, AL10}
import org.lwjgl.openal.AL10._
import org.lwjgl.util.WaveData
import org.newdawn.slick.openal.{OggInputStream, OggDecoder}
import java.nio.{IntBuffer, FloatBuffer, ByteBuffer}
import maackle.{util => maack}

trait Sound {
  def playing:Boolean
  def play(forceRestart:Boolean=false):Sound
  def pause():Sound
  def stop():Sound
  def gain(level:Float):Sound
  def loop(on:Boolean):Sound
}

class ALsource() {

  // currently this is not set up to ever change position, velocity, or orientation, or even to be used in 3D worlds.

  private var buffer: IntBuffer = BufferUtils.createIntBuffer(1)
  private var sourcebuf: IntBuffer = BufferUtils.createIntBuffer(1)
  private var sourcePos: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
  private var sourceVel: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))

  def setPos(pos:(Float,Float,Float)) {
    sourcePos.rewind()
    sourcePos.put(Array(pos._1, pos._2, pos._3))
    sourcePos.flip()
  }
  def setVel(v:(Float,Float,Float)) {
    sourceVel.rewind()
    sourceVel.put(Array(v._1, v._2, v._3))
    sourceVel.flip()
  }

  setPos(0,0,0)
  setVel(0,0,0)

  def playing = alGetSourcei(id, AL_SOURCE_STATE) == AL_PLAYING

  def check(complaint:String="reality check"):Boolean = {
    import AL10._
    val err = AL10.alGetError
    val reason = err match {
      case AL_NO_ERROR => return true
      case AL_INVALID_NAME => "Invalid name parameter."
      case AL_INVALID_ENUM => "Invalid parameter."
      case AL_INVALID_VALUE => "Invalid enum parameter value."
      case AL_INVALID_OPERATION => "Illegal call."
      case AL_OUT_OF_MEMORY => "Unable to allocate memory."
      case _ => "Unknown error"
    }
    throw new Exception("%s - al error: %s (%d)".format(complaint, reason, err))
  }

  def id:Int = sourcebuf.get(0)

  def logg(is:InputStream) {
    val ois = new OggInputStream(is)
    val bytes = new Array[Byte](ois.getLength)
    ois.read(bytes)
    val buf = ByteBuffer.allocateDirect(bytes.length)
    buf.put(bytes)
    buf.flip()
    load(AL10.AL_FORMAT_VORBIS_EXT, buf, ois.getRate)
  }
  def loadOgg(is:InputStream) {
    val dec = new OggDecoder()
    val ogg = dec.getData(is)
    val fmt = if(ogg.channels > 1) AL10.AL_FORMAT_STEREO16 else AL10.AL_FORMAT_MONO16
    load(fmt, ogg.data, ogg.rate)
  }

  def loadWave(is:InputStream): ALsource = {
    val wav = WaveData.create(is)
    load(wav.format, wav.data, wav.samplerate)
  }

  private def load(fmt:Int, dat:ByteBuffer, rate:Int):ALsource = {
    AL10.alGenBuffers(buffer)
    check("11")
    AL10.alBufferData(buffer.get(0), fmt, dat, rate)
    check("22")
    AL10.alGenSources(sourcebuf)
    check("33")
    AL10.alSourcei(id, AL10.AL_BUFFER, buffer.get(0))
    check("44")
    AL10.alSourcef(id, AL10.AL_PITCH, 1.0f)
    AL10.alSourcef(id, AL10.AL_GAIN, 1.0f)
    AL10.alSource(id, AL10.AL_POSITION, sourcePos)
    AL10.alSource(id, AL10.AL_VELOCITY, sourceVel)
    check("55")
    this
  }

  private def setf(attr:Int, v:Float) = AL10.alSourcef(sourcebuf.get(0), attr, v)
  private def seti(attr:Int, v:Int) = AL10.alSourcei(sourcebuf.get(0), attr, v)

  def gain(v:Float):ALsource = { setf(AL10.AL_GAIN, v); this}
  def loop(v:Boolean):ALsource = { seti(AL10.AL_LOOPING, if(v) 1 else 0); this }
  def play(forceRestart:Boolean=false) {
    if(forceRestart || !playing) {
      AL10.alSourcePlay(sourcebuf.get(0))
    }
    this
  }
  def pause() {
    AL10.alSourcePause(sourcebuf.get(0))
    this
  }
  def stop() {
    AL10.alSourceStop(sourcebuf.get(0))
    this
  }
  def destroy() {
    stop()
    AL10.alDeleteSources(sourcebuf)
    AL10.alDeleteBuffers(buffer)
  }
}



object NullSound extends Sound {
  override def playing = false
  override def play(restart:Boolean=false) = this
  override def pause() = this
  override def stop() = this
  override def gain(g:Float) = this
  override def loop(v:Boolean):Sound = this
  override def toString = "NullSource"
}

object NullSource extends ALsource {
  override def playing = false
  override def check(complaint:String="reality check") = false
  override val id:Int = -1
  override def loadOgg(is:InputStream) = this
  override def loadWave(is:InputStream) = this
  override def play(restart:Boolean=false) = this
  override def pause() = this
  override def stop() = this
  override def destroy() {}
  override def gain(g:Float) = this
  override def loop(v:Boolean):ALsource = this
  override def toString = "NullSource"
}

class SoundStore(private val nullify:Boolean=false) {
  //   var buffer: IntBuffer = BufferUtils.createIntBuffer(1)
  //   var sourcebuf: IntBuffer = BufferUtils.createIntBuffer(1)

  private var sources = Map[ String, ALsource ]()

  def apply(name:String):ALsource = if(SoundStore.enabled) sources(name) else NullSource
  def source(name:String):ALsource = if(SoundStore.enabled) sources(name) else NullSource

  def initialized = SoundStore.initialized_?

  def addSource(path:String, name:String=null, loop:Boolean=false): ALsource = {
    if(!SoundStore.enabled) return NullSource
    require(initialized, "Must call SoundStore.initialize()")
    val maack.pathPattern(dir, filename, ext) = path
    val s = if(nullify) NullSource else new ALsource()

    try {
      val stream = maack.getStream(path)
      ext.toUpperCase match {
        case "WAV" => s.loadWave(stream)
        case "OGG" => s.loadOgg(stream)
      }
      s.loop(loop)
    }
    catch {
      case e:NullPointerException => println("couldn't load sound clip %s".format(path))
    }
    val key = if(name==null) filename else name
    if(sources.contains(key)) throw new Exception("A sound named \"%s\" already exists in this SoundStore")
    sources += key -> s
    return s
  }

  def addNull(name:String): ALsource = {
    if(!SoundStore.enabled) return NullSource
    sources += name -> NullSource
    return NullSource
  }

  private def allIds = IntBuffer.wrap(sources.values.map(_.id).toArray)

  def playAll() { if(!SoundStore.enabled) return; AL10.alSourcePlay(allIds) }
  def pauseAll() { if(!SoundStore.enabled) return; AL10.alSourcePause(allIds) }
  def stopAll() { if(!SoundStore.enabled) return; AL10.alSourceStop(allIds) }

  def destroy() {
    if(!SoundStore.enabled) return
    sources.foreach(_._2.destroy())
  }
}


object SoundStore {
  var listenerPos: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
  var listenerVel: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
  /**Orientation of the listener. (first 3 elements are "looking towards", second 3 are "up")
      Also note that these should be units of '1'. */
  var listenerOri: FloatBuffer = BufferUtils.createFloatBuffer(6).put(Array[Float](0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f))
  listenerPos.flip
  listenerVel.flip
  listenerOri.flip
  private var initialized_? = false
  val oggDecoder = new OggDecoder
  def init() {
    if(!SoundStore.enabled) return
    try {
      AL.create(null, 44100, 15, true);
    }
    catch {
      case le: LWJGLException => {
        le.printStackTrace()
        return
      }
    }
    initialized_? = true
    AL10.alGetError
    AL10.alListener(AL10.AL_POSITION, listenerPos)
    AL10.alListener(AL10.AL_VELOCITY, listenerVel)
    AL10.alListener(AL10.AL_ORIENTATION, listenerOri)
    enabled = true
  }
  private var enabled = true
  def disable() {
    enabled = false
  }
}