package trainer

import java.io.File

import akka.actor.{ActorSystem, Props}
import trainer.core.{CsvWriter, Driver}
import trainer.core.Driver.Tables

/**
  * Created by mishrapaw on 6/16/17.
  */
class Main(args: Array[String]) {

  val cli = Cli(args)

  def run(): Unit = {
    val inputFilePath = cli.input.fold("No value")(x => x)
    val outputLocation = cli.output.fold("/Users/mishrapaw/Downloads")(x => x)
    val inputJson = io.Source.fromFile(new File(inputFilePath)).mkString
    val tables = Parser.map(inputJson)

    implicit val system = ActorSystem()
    val driver = system.actorOf(Driver.props(outputLocation), "driver")
    driver ! Tables(tables)
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    val app = new Main(args)
    app.run()
  }
}
