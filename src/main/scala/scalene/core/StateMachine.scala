package scalene.core

import scalene.traits.State

class StateMachine(startState:State) {
  assert(startState != null)
  protected val stack = collection.mutable.Stack[State](startState)

  def current = {
    assert(!stack.isEmpty)
    stack.top
  }

  def change(s:State) {
    assert(!stack.isEmpty)
    stack.pop()
    stack.push(s)
  }

  def push(s:State) {
    stack.push(s)
  }

  def pop() = {
    assert(!stack.tail.isEmpty)
    stack.pop()
  }
}
