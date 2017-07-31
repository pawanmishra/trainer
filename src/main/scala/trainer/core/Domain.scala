package trainer.core

/**
  * Created by mishrapaw on 6/16/17.
  */
case class Table(name: String, rowCount: Int, columns: List[Column])
case class Column(name: String, handler: Map[String, String]) {

  lazy val format : String = handler("format")

  lazy val columnHandler : ColumnHandler = {
    require(handler.contains("format"), "Column definition requires format field.")
    format match {
      case "Integer" => new ColumnHandler(handler) with IntHandler
      case "Boolean" => new ColumnHandler(handler) with BooleanHandler
      case "Guid" => new ColumnHandler(handler) with GuidHandler
      case "Reference" => new ColumnHandler(handler) with ReferenceHandler
      case "Regex" => new ColumnHandler(handler) with RegexHandler
      case _ => throw new IllegalArgumentException(s"Invalid column format value : ${handler("format")}")
    }
  }
}

class ColumnHandler(val properties: Map[String, String]) {

  def getNullIndices(nullPercentage: Int, totalCount: Int): Set[Int] = {
    val numOfRecords = (nullPercentage * totalCount) / 100
    val indices = for (_ <- 1 to numOfRecords) yield scala.util.Random.nextInt(totalCount)
    indices.toSet
  }

  def generateData(rowCount: Int): List[Option[String]] = {
    val nullIndices = getNullIndices(properties("nullPercentage").toInt, rowCount)
    val data = for (i <- 1 to rowCount) yield if (nullIndices.contains(i)) None else generate()
    data.toList
  }

  def generateData(rowCount: Int, values: List[Option[String]]): List[Option[String]] = {
    val nullIndices = getNullIndices(properties("nullPercentage").toInt, rowCount)
    val data = for (i <- 1 to rowCount) yield if (nullIndices.contains(i)) None else generate(values)
    data.toList
  }

  protected def generate(): Option[String] = None

  protected def generate(values: List[Option[String]]): Option[String] = None
}


object FekuImplicits {

  /*implicit def stringToInt(input: String)(implicit default: Int = 0): Int = {
    val intOption = Try { input.toInt }
    intOption.toOption.fold(default)(x => x)
  }*/

}
