package scalene.core

import traits.Render

trait BareState {

  protected def onEnter(bloc: =>Unit) {}
  protected def onExit(bloc: =>Unit) {}
}

abstract class State(val app:ScaleneApp) extends ThingStore with Render {
  def this(domain:Domain) = this(domain.app)
  val view:ViewScheme

  protected def onEnter(bloc: =>Unit) {}
  protected def onExit(bloc: =>Unit) {}



  def render() {
    if(view!=null) view.__render()
  }
//  def sound:Option[SoundStore]
}

object State {

  object CrashAndBurn extends State(null.asInstanceOf[ScaleneApp]) {
    val view = null
  }

  class StateMachine(startState:State) {
    assert(startState != null)
    private val stack = collection.mutable.Stack[State]()

    push(startState)

    def current = {
      assert(!stack.isEmpty)
      stack.top
    }

    def change(s:State) {
      assert(!stack.isEmpty)
      pop()
      push(s)
    }

    def push(s:State) {
      stack.push(s)
      current.onEnter()
    }

    def pop() {
      assert(!stack.tail.isEmpty)
      val top = stack.pop()
      top.onExit()
    }
  }
}