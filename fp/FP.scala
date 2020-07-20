import scala.annotation.tailrec

object FP {
  // // // Immutablility // // // 
  val emptyList: List[String] = List()
  // Prepend a new element. Build a new value!
  val oneElementList: List[String] = "first" :: emptyList // List("first")

  // // // Referential transparency // // //
  // Good
  val plusOne = (x: Int) => x + 1
  plusOne(1) // returns always the same result with the same input

  // Bad
  val plusRandom = (x: Int) => x + scala.util.Random.nextInt()
  plusRandom(1) // Same input returns different value

  // // // Recursion // // //
  // Normal recursion
  def sum(ns: List[Int]): Int = 
    ns match {
      case Nil => 0
      case h :: tl => h + sum(tl)
    }

  // Tail recusion
  @tailrec
  def sumAccumulator(ns: List[Int], accum: Int): Int =
    ns match {
      case Nil => accum
      case h:: tl => sumAccumulator(tl, accum + h)
    }


}