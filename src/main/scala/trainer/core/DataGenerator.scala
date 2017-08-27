package trainer.core

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.ExecutionContext

/**
  * Created by mishrapaw on 6/17/17.
  */
class DataGenerator(table: Table, outputActor: ActorRef) extends Actor {
  import DataGenerator._
  implicit val ec = ExecutionContext.global

  private[this] val data = scala.collection.mutable.Map[String, Vector[Any]]()
  println(s"Starting actor ${context.self.path}")

  private[this] def getReferencedData(column: Column): Unit = {
    val referencedTable = column.handler("table")
    val referencedColumn = column.handler("column")
    val sibling = context.actorSelection(s"../$referencedTable")
    sibling ! Fetch(column.name, referencedColumn)
  }

  private[this] def get(sourceColumn: String, targetColumn: String): Received = {
    Received(sourceColumn, data(targetColumn))
  }

  private[this] def sendToWriter() : Unit = {
    if (table.columns.forall(x => data.contains(x.name) && data(x.name).size == table.rowCount))
      outputActor ! Record(table.name, data.toMap)
  }

  override def receive: Receive = {
    case Generate => {
      table.columns.filter(x => x.format == "Reference").foreach(getReferencedData)
      table.columns.filter(x => x.format != "Reference").foreach(column => {
        val columnValues = column.columnHandler.generateData(table.rowCount)
        data.put(column.name, columnValues)
      })
      sendToWriter()
    }
    case Fetch(sourceColumn, targetColumn) => {
      sender ! get(sourceColumn, targetColumn)
    }
    case Received(column, record) => {
      val mappedData = table.columns.find(_.name.equalsIgnoreCase(column)).get.columnHandler.generateData(table.rowCount, record)
      data.put(column, mappedData)
      sendToWriter()
    }
  }
}

object DataGenerator {
  def props(table: Table, outputActor: ActorRef) = Props(new DataGenerator(table, outputActor))
  case object Generate
  case class Fetch(sourceColumn: String, targetColumn: String)
  case class Received(column: String, record: Vector[Any])
}
