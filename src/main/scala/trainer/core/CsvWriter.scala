package trainer.core

import java.io.{FileWriter, PrintWriter}

import akka.actor.{Actor, PoisonPill, Props}

import scala.util.control.NonFatal

/**
  * Created by mishrapaw on 6/22/17.
  */
case class Record(table: String, data: Map[String, List[Option[String]]])

class CsvWriter(outputLocation: String) extends Actor {

  override def receive : Receive = {
    case Record(table, content) =>
      val head = content.values.head
      val tail = content.values.tail
      //val result = tail.foldLeft(head)((a, b) => a.zip(b).map(x => Some(x._1.fold("")(a => a) + "|" + x._2.fold("")(b => b))))
      manage(new PrintWriter(new FileWriter(s"$outputLocation/$table.txt"))) { writer =>
        writer.write(content.keys.mkString("|"))
        writer.write("\n")

        for {
          i <- content.values.head.indices
        } {
          val index = i
          val result = content.values.map(x => x(index).getOrElse("")).mkString("|")
          writer.write(result)
          writer.write("\n")
        }

        /*result.foreach(x => {
          writer.write(x.fold("")(a => a))
          writer.write("\n")
        })*/
      }
      sender() ! PoisonPill
  }
}

object CsvWriter {
  def props(outputLocation: String) = Props(new CsvWriter(outputLocation))
}

object manage {
  def apply[R <: { def close():Unit }, T](resource: => R)(f: R => T) = {
    var res: Option[R] = None
    try {
      res = Some(resource)         // Only reference "resource" once!!
      f(res.get)
    } catch {
      case NonFatal(ex) => println(s"Non fatal exception! $ex")
    } finally {
      if (res.isDefined) {
        println(s"Closing resource...")
        res.get.close
      }
    }
  }
}
