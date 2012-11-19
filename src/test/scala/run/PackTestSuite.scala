package run

import org.scalatest.FunSuite
import collection.SortedSet
import packer.BadPlan

class PackTestSuite extends FunSuite {

  trait V {
    val plan = new BadPlan
    val ps = Array(
      plan.AnchorPoint((0,0), (None,None)),
      plan.AnchorPoint((10,0), (None,None)),
      plan.AnchorPoint((0,20), (None,None)),
      plan.AnchorPoint((10,20), (None,None))
    )
  }
  test("add remove") {
    new V {
      var ss = SortedSet.empty(new Ordering[plan.Point] {
        def compare(a:plan.Point, b:plan.Point) = a.x compare b.x
      })
      ss += (ps(0))
      println(ss)
      ss += ps(1)
      println(ss)
      ss += ps(2)
      println(ss)
      ss += ps(3)
      println(ss)

    }
  }

}
