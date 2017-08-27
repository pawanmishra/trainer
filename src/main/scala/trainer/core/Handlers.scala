package trainer.core

import java.util.UUID
import java.util.concurrent.{ThreadLocalRandom, TimeUnit}

import nl.flotsam.xeger.Xeger
import org.joda.time.{DateTime, Days}

import scala.util.Random

/**
  * Created by mishrapaw on 6/22/17.
  */
trait IntHandler {
  self: ColumnHandler =>

  private val minValue = self.properties.getOrElse("minvalue", "0").toInt
  private val maxValue = self.properties.getOrElse("maxvalue", "10000").toInt

  override def generate(rowCount: Int, nullIndices: Set[Int]) : Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        minValue + scala.util.Random.nextInt((maxValue - minValue) + 1)
    }
    data.toVector
  }
}

trait DoubleHandler {
  self: ColumnHandler =>

  private val minValue = self.properties.getOrElse("minvalue", "0").toDouble
  private val maxValue = self.properties.getOrElse("maxvalue", "10000").toDouble

  override def generate(rowCount: Int, nullIndices: Set[Int]) : Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        ThreadLocalRandom.current().nextDouble(minValue, maxValue)
    }
    data.toVector
  }
}

trait BooleanHandler {
  self: ColumnHandler =>

  override def generate(rowCount: Int, nullIndices: Set[Int]) : Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        math.random < 0.50
    }
    data.toVector
  }
}

trait GuidHandler {
  self: ColumnHandler =>

  override def generate(rowCount: Int, nullIndices: Set[Int]) : Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        java.util.UUID.randomUUID
    }
    data.toVector
  }
}

trait ConstantHandler {
  self: ColumnHandler =>
  val value = self.properties("value")

  override def generate(rowCount: Int, nullIndices: Set[Int]) : Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        value
    }
    data.toVector
  }
}

trait ReferenceHandler {
  self: ColumnHandler =>

  override def generate(rowCount: Int, nullIndices: Set[Int], values: Vector[Any]): Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        values(scala.util.Random.nextInt(values.size))
    }
    data.toVector
  }
}

trait ListHandler {
  self: ColumnHandler =>

  private val value = self.properties("listitems")
  private val delimiter = self.properties("seperator")
  private val items = value.split(delimiter).toVector

  override def generate(rowCount: Int, nullIndices: Set[Int]): Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        items(scala.util.Random.nextInt(items.length))
    }
    data.toVector
  }
}

trait RegexHandler {
  self: ColumnHandler =>

  private val pattern = self.properties("expression")
  private val xeger = new Xeger(pattern)

  override def generate(rowCount: Int, nullIndices: Set[Int]): Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        xeger.generate()
    }
    data.toVector
  }
}

trait FileHandler {
  self: ColumnHandler =>

  private val items = scala.io.Source.fromFile(self.properties("path")).getLines().toList
  override def generate(rowCount: Int, nullIndices: Set[Int]): Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        items(scala.util.Random.nextInt(items.length))
    }
    data.toVector
  }
}

trait DateTimeHandler {
  self: ColumnHandler =>

  private val start = self.properties.getOrElse("minvalue", "2000-01-01")
  private val end = self.properties.getOrElse("maxvalue", "2017-01-01")

  private val startDate = DateTime.parse(start)
  private val endDate = DateTime.parse(end)
  private val daysInBetween = Days.daysBetween(startDate, endDate).getDays
  private val sampleCount = scala.util.Random.nextInt(daysInBetween)
  lazy val values = for (_ <- 1 to sampleCount) yield startDate.plusDays(scala.util.Random.nextInt(daysInBetween)).toString("yyyy-MM-dd")

  override protected def generate(rowCount: Int, nullIndices: Set[Int]): Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else
        values(scala.util.Random.nextInt(values.size))
        //startDate.plusDays(scala.util.Random.nextInt(daysInBetween)).toString("yyyy-MM-dd")
    }
    data.toVector

  }
}

trait TextHandler {
  self: ColumnHandler =>

  private val minLength = self.properties.get("minlength").fold(5)(x => x.toInt)
  private val maxLength = self.properties.get("maxlength").fold(10)(x => x.toInt)
  private val lengthDif = maxLength - minLength

  lazy private val values = for (_ <- 1 to 10) yield {
    Random.alphanumeric.take(minLength + lengthDif).mkString
  }

  override def generate(rowCount: Int, nullIndices: Set[Int]): Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else{
        values(scala.util.Random.nextInt(values.size))
      }
    }
    data.toVector
  }
}

trait SequenceHandler {
  self: ColumnHandler =>

  private var increment = self.properties.getOrElse("increment", "0").toInt
  private val startValue = self.properties.getOrElse("startvalue", "0").toInt

  override protected def generate(rowCount: Int, nullIndices: Set[Int]): Vector[Any] = {
    val data = for (i <- 1 to rowCount) yield {
      if (nullIndices.contains(i)) ""
      else {
        increment = increment + 1
        startValue + increment
      }
    }
    data.toVector
  }
}

object Timer {
  def time[R](block: => R)(implicit message: String = ""): R = {
    println(message)
    val t0 = System.currentTimeMillis()
    val result = block    // call-by-name
    val t1 = System.currentTimeMillis()
    val timeInseconds = TimeUnit.SECONDS.convert(t1 - t0, TimeUnit.MILLISECONDS)
    println(s"Elapsed time: " + timeInseconds + " seconds")
    result
  }
}

class Test(val num: String) extends AnyVal