def divBy(a: Int, ds: List[Int]): List[Int] = {
  ds.map(d => a / d)
}

val first = (1 until 10).toList
val num = 50

divBy(num, first)

val second = (0 until 10).toList
divBy(num, second)