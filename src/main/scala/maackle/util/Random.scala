package maackle.util

object Random {
  val rand = new scala.util.Random()

  def radians = uniform(0, math.Pi*2)

  def uniform(lo: Double = 0.0, hi: Double = 1.0): Double = (hi - lo) * rand.nextDouble + lo
  def uniform(lo: Float, hi: Float): Float = (hi - lo) * rand.nextFloat + lo
  def uniform(lo: Int, hi: Int): Int = (lo + rand.nextInt(hi-lo) )

  def gaussian(mean: Double, std: Double): Double = rand.nextGaussian * std + mean
}