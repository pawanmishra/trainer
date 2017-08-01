package trainer.core

import akka.actor.{Actor, ActorRef, Props}

/**
  * Created by mishrapaw on 6/17/17.
  */
class Driver(outputLocation: String) extends Actor {
  import Driver._

  var tableActors = scala.collection.mutable.Map[String, ActorRef]()

  override def receive: Receive = {
    case Tables(tables) => {
      tables.foreach(table => {
        val outputActor = context.actorOf(CsvWriter.props(outputLocation), table.name + "_outputActor")
        val actor = context.actorOf(DataGenerator.props(table, outputActor), table.name)
        tableActors.put(table.name, actor)
      })
      tableActors.values.foreach(x => x ! DataGenerator.Generate)
    }
  }
}

object Driver {
  def props(outputLocation: String) = Props(new Driver(outputLocation))
  case class Tables(tables: List[Table])
}
