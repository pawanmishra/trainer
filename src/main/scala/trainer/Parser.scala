package trainer

import trainer.core.{ColumnHandler, Table}
import org.json4s._
import org.json4s.native.JsonMethods._

/**
  * Created by mishrapaw on 6/16/17.
  */
object Parser {

  def map(inputJson: String): List[Table] = {
    implicit val formats = DefaultFormats

    val input = parse(inputJson)
    val tables = (input \\ "tables").extract[List[Table]]

    tables
  }
}
