package trainer

import org.apache.commons.cli.{CommandLine, DefaultParser, Options, ParseException, UnrecognizedOptionException, Option => CliOption}

/**
  * Created by mishrapaw on 6/16/17.
  */
class Cli(args: Array[String]) {

  import Cli._

  val cmd = parse()

  private def parse(): Option[CommandLine] = {
    val parser = new DefaultParser
    Some(parser.parse(options, args))
  }

  private def fetchValue(option: String): Option[String] = {
    cmd.fold[Option[String]](None)(x => if (x.hasOption(option)) Some(x.getOptionValue(option)) else None)
  }

  def input: Option[String] = fetchValue(INPUT_JSON_OPTION)
  def output: Option[String] = fetchValue(OUTPUT_CSV_OPTION)
}

object Cli {

  private val INPUT_JSON_OPTION = "input"
  private val OUTPUT_CSV_OPTION = "output"

  def apply(args: Array[String]): Cli = new Cli(args)

  def options: Options = {
    val options = new Options()
    val input = CliOption.builder().argName("i").desc("Input json file containing data model definition.")
            .required(true).numberOfArgs(1).longOpt(INPUT_JSON_OPTION).build()
    val output = CliOption.builder().argName("o").desc("Location where output files will be written.")
              .required(true).numberOfArgs(1).longOpt(OUTPUT_CSV_OPTION).build()

    options.addOption(input)
    options.addOption(output)

    options
  }
}

