package trainer.core

import java.util.concurrent.TimeUnit

import nl.flotsam.xeger.Xeger

/**
  * Created by mishrapaw on 6/22/17.
  */
trait IntHandler extends Timer {
  self: ColumnHandler =>

  override def generate() : Option[String] = Some("2")
}

trait BooleanHandler {
  self: ColumnHandler =>

  override def generate() : Option[String] = Some(true.toString)
}

trait GuidHandler {
  self: ColumnHandler =>

  override def generate() : Option[String] = Some(java.util.UUID.randomUUID.toString)
}

trait ConstantHandler {
  self: ColumnHandler =>

  override def generate() : Option[String] = Some(self.properties("value"))
}

trait ReferenceHandler {
  self: ColumnHandler =>

  override def generate(values: List[Option[String]]): Option[String] = values(scala.util.Random.nextInt(values.size))
}

trait ListHandler {
  self: ColumnHandler =>

  private val value = self.properties("items")
  private val delimiter = self.properties("delimiter")
  private val items = value.split(delimiter).map(Some(_))

  override def generate(): Option[String] = items(scala.util.Random.nextInt(items.length))
}

trait RegexHandler {
  self: ColumnHandler =>

  private val pattern = self.properties("pattern")
  private val xeger = new Xeger(pattern)

  override def generate(): Option[String] = Some(xeger.generate())
}

trait FileHandler {
  self: ColumnHandler =>

  private val items = scala.io.Source.fromFile(self.properties("path")).getLines().toList
  override def generate(): Option[String] = Some(items(scala.util.Random.nextInt(items.length)))
}

trait TextHandler {
  self: ColumnHandler =>

  private val minLength = self.properties.get("min").fold(5)(x => x.toInt)
  private val maxLength = self.properties.get("max").fold(10)(x => x.toInt)
  private val pattern = s"[a-zA-Z ]{$minLength,$maxLength}"
  private val xeger = new Xeger(pattern)

  override def generate(): Option[String] = Some(xeger.generate())
}

trait Timer {
  def time[R](block: => R)(implicit message: String = ""): R = {
    println(message)
    val t0 = System.currentTimeMillis()
    val result = block    // call-by-name
    val t1 = System.currentTimeMillis()
    val timeInseconds = TimeUnit.SECONDS.convert(t1 - t0, TimeUnit.MILLISECONDS)
    println("Elapsed time: " + timeInseconds + " seconds")
    result
  }
}