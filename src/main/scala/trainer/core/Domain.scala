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
      case "Text" => new ColumnHandler(handler) with TextHandler
      case "Constant" => new ColumnHandler(handler) with ConstantHandler
      case "List" => new ColumnHandler(handler) with ListHandler
      case "DateTime" => new ColumnHandler(handler) with DateTimeHandler
      case "Sequence" => new ColumnHandler(handler) with SequenceHandler
      case "Double" => new ColumnHandler(handler) with DoubleHandler
      case _ => throw new IllegalArgumentException(s"Invalid column format value : ${handler("format")}")
    }
  }
}

class ColumnHandler(val properties: Map[String, String]) {

  import Timer._

  private def getNullIndices(nullPercentage: Int, totalCount: Int): Set[Int] = {
    val numOfRecords = (nullPercentage * totalCount) / 100
    val indices = for (_ <- 1 to numOfRecords) yield scala.util.Random.nextInt(totalCount)
    indices.toSet
  }

  def generateData(rowCount: Int): Vector[Any] = {
      val nullPercentage = properties.getOrElse("nullPercentage", "0").toInt
      val nullIndices = getNullIndices(nullPercentage, rowCount)
      generate(rowCount, nullIndices)
  }

  def generateData(rowCount: Int, values: Vector[Any]): Vector[Any] = {
      val nullPercentage = properties.getOrElse("nullPercentage", "0").toInt
      val nullIndices = getNullIndices(nullPercentage, rowCount)
      val notNullValues = values.filter(!_.toString.isEmpty)
      generate(rowCount, nullIndices, notNullValues)
  }

  protected def generate(rowCount: Int, nullIndices: Set[Int]): Vector[Any] = Vector.empty

  protected def generate(rowCount: Int, nullIndices: Set[Int], values: Vector[Any]): Vector[Any] = Vector.empty
}