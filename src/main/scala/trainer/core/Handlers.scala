package trainer.core

import java.util.concurrent.TimeUnit

import nl.flotsam.xeger.Xeger
import org.joda.time.{DateTime, Days}

/**
  * Created by mishrapaw on 6/22/17.
  */
trait IntHandler {
  self: ColumnHandler =>

  private val minValue = self.properties.getOrElse("minvalue", "0").toInt
  private val maxValue = self.properties.getOrElse("maxvalue", "10000").toInt

  override def generate() : String = (minValue + scala.util.Random.nextInt((maxValue - minValue) + 1)).toString
}

trait BooleanHandler {
  self: ColumnHandler =>

  override def generate() : String = (math.random < 0.50).toString
}

trait GuidHandler {
  self: ColumnHandler =>

  override def generate() : String = java.util.UUID.randomUUID.toString
}

trait ConstantHandler {
  self: ColumnHandler =>

  override def generate() : String = self.properties("value")
}

trait ReferenceHandler {
  self: ColumnHandler =>

  override def generate(values: Vector[String]): String = values(scala.util.Random.nextInt(values.size))
}

trait ListHandler {
  self: ColumnHandler =>

  private val value = self.properties("listitems")
  private val delimiter = self.properties("seperator")
  private val items = value.split(delimiter)

  override def generate(): String = items(scala.util.Random.nextInt(items.length))
}

trait RegexHandler {
  self: ColumnHandler =>

  private val pattern = self.properties("expression")
  private val xeger = new Xeger(pattern)

  override def generate(): String = xeger.generate()
}

trait FileHandler {
  self: ColumnHandler =>

  private val items = scala.io.Source.fromFile(self.properties("path")).getLines().toList
  override def generate(): String = items(scala.util.Random.nextInt(items.length))
}

trait DateTimeHandler {
  self: ColumnHandler =>

  private val start = self.properties.getOrElse("minvalue", "2000-01-01")
  private val end = self.properties.getOrElse("maxvalue", "2017-01-01")

  private val startDate = DateTime.parse(start)
  private val endDate = DateTime.parse(end)
  private val daysInBetween = Days.daysBetween(startDate, endDate).getDays

  override protected def generate(): String = startDate.plusDays(scala.util.Random.nextInt(daysInBetween)).toString("yyyy-MM-dd")
}

trait TextHandler {
  self: ColumnHandler =>

  private val minLength = self.properties.get("minlength").fold(5)(x => x.toInt)
  private val maxLength = self.properties.get("maxlength").fold(10)(x => x.toInt)
  private val pattern = s"[a-zA-Z]{$minLength,$maxLength}"
  private val xeger = new Xeger(pattern)

  override def generate(): String = xeger.generate()
}

trait SequenceHandler {
  self: ColumnHandler =>

  private var increment = self.properties.getOrElse("increment", "0").toInt
  private val startValue = self.properties.getOrElse("startvalue", "0").toInt

  override protected def generate(): String = {
    increment = increment + 1
    (startValue + increment).toLong.toString
  }
}

object Timer {
  def time[R](block: => R)(implicit message: String = ""): R = {
    val t0 = System.currentTimeMillis()
    val result = block    // call-by-name
    val t1 = System.currentTimeMillis()
    val timeInseconds = TimeUnit.SECONDS.convert(t1 - t0, TimeUnit.MILLISECONDS)
    println(s"$message : Elapsed time: " + timeInseconds + " seconds")
    result
  }
}