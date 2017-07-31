package trainer.core

import nl.flotsam.xeger.Xeger
import org.scalatest._

/**
  * Created by mishrapaw on 6/16/17.
  */
class ParserTest extends FunSuite with MustMatchers {

  test("Parser should parse valid json into strongly typed data model") {
    val random = new java.util.Random()
    val xeg = new Xeger("""^[a-zA-Z ]{100,200}$""", random)
    val data = xeg.generate()
    println(data)
  }

  test("Test for merging List of items") {
    val items = List(List(Some(1), Some(2), Some(3)), List(Some(4), Some(5), Some(6)), List(Some(7), Some(8), Some(9)))
    val head = items.head
    val tail = items.tail
    val result = tail.foldLeft(head)((a, b) => a.zip(b).map(x => Some(x._1.fold(1)(a => a) * x._2.fold(1)(b => b))))
    println(result)
  }

  def getNullIndices(nullPercentage: Int, totalCount: Int): Set[Int] = {
    val numOfRecords = (nullPercentage * totalCount) / 100
    val indices = for (_ <- 1 to numOfRecords) yield scala.util.Random.nextInt(totalCount)
    indices.toSet
  }

  test("Generate million ints") {
    val n = 1000000
    val nullIndices = getNullIndices(20, n)
    val data = for (i <- 1 to n) yield if (nullIndices.contains(i)) None else Some(i.toString)
    data.toList
  }
}
