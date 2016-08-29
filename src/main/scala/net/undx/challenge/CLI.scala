package net.undx.challenge

import java.io.File

import scopt._

/**
  * Config class for `scopt`. 
  * This class will store command-line options.
  */
case class Config(
  /** File to process */ 
  filename: String = "",
  /** filter on the department */
  department: String = "",
  /** filter on room(s) */
  rooms: Seq[String] = Seq(),
  /** path to write generated files */ 
  outputPath: String = ""
)

/**
  * The Command-Line Interface singleton for processing the challenge files.
  * 
  */
object CLI {

  /** All valid rooms' letters. (const) */
  val ValidRooms = (for (r <- 'A' to 'F') yield r.toString).toArray

  /** All valid departments. (const) */
  val ValidDeparments = for (d <- 1 to 10) yield s"HR${d}"

  /**
    * Check and get the room's full name to process.
    * This method always return at least one Room. If empty or invalid, returns
    * all rooms.
    * @return if rFilter is not empty, the selected and ''valid'' rooms
    *         if rFilter is empty, all available rooms.
    * @param rFilter Room filter. Abbreviated format. ie: "A", "B",...
    */
  def validateAndBuildRooms(rFilter: Seq[String]): Seq[String] = {
    val roomALL = ValidRooms.map("room" + _)
    rFilter.size match {
      case f if f > 0 => {
        val rooms = rFilter
          .filter { ValidRooms.contains(_) }
          .map("room" + _)
          .distinct //In case we've the same room many times.
        if (rooms.size == 0) roomALL
        else rooms
      }
      case _ => roomALL
    }
  }

  /**
    * Check the department filter.
    *
    * @return The department to filter if this one is ''valid''. Otherwise, it returns an empty string (no filter).
    * @param departement The name of the department.
    */
  def validateDepartment(department: String): String = {
    ValidDeparments.contains(department) match {
      case true => department
      case false if department.isEmpty => ""
      case false => {
        Console.err.println(s"Invalid Department '$department'. Skipping filter.")
        ""
      }
    }
  }

  /**
    * Process CLI arguments. 
    * @return the parsed arguments as a Config class or None(incorrect parameters or help).
    * @param arguments the arguments passed through command-line.
    */
  def parseCLIOptions(arguments: Array[String]): Option[Config] = {
    val parser = new scopt.OptionParser[Config]("challenge") {
      // Options available on command-line : 
      head("challenge", "1.0.0")
      opt[String]('f', "filename").required().valueName("<file>").action((x, c) => c.copy(filename = x))
        .text("input filename to process.")
      opt[String]('d', "department").valueName("<dept>").action((x, c) => c.copy(department = x))
        .text("department selection. You can select only one in HR1, HR2, etc.")
      opt[Seq[String]]('r', "rooms").valueName("<r1,r2,...>").action((x, c) => c.copy(rooms = x))
        .text("rooms to include (A to F).")
      opt[String]('o', "output").valueName("<path>").action((x, c) => c.copy(outputPath = x))
        .text("where to write files produced. Defaults to filename's folder.")
      help("help").text("prints this usage text")
      // custom behaviors
      override def terminate(exitState: Either[String, Unit]): Unit = () // don't do a Sys.exit(0)
    }

    parser.parse(arguments, Config()) match {
      case Some(config) =>
        val filename = config.filename
        val department = validateDepartment(config.department)
        val rooms = validateAndBuildRooms(config.rooms)
        var outputPath = config.outputPath
        // Input file exists ?
        val checkInputFile = new File(filename)
        if (!checkInputFile.exists) {
          Console.err.println(s"The input file doesn't exists or is not provided. Process will stop.")
          return None
        }
        // output path defaults to input file if invalid or not defined 
        if (outputPath.isEmpty || !new File(outputPath).exists) {
          outputPath = new File(checkInputFile.getCanonicalPath).getParent
        }
        Some(Config(filename, department, rooms, outputPath))
      case _ => None
    }
  }

  /**
    * Application's entry point.
    * Process command-line options, If all is ok, calls the Worker
    * which reads the input file and writes the produced files.
    */
  def main(args: Array[String]): Unit = {
    val config:Option[Config] = parseCLIOptions(args)
    config match {
      case Some(cfg) => {
        try {
          Worker.process(cfg.filename, cfg.department, cfg.rooms, cfg.outputPath)          
        } catch {
          case wex: WorkerException => {
            Console.err.println(s"Please check your input file : $wex")
          }
          case oex: Exception => {
            Console.err.println(oex)
          }
        }
      }
      case _ => {} // nop
    }
  }
}
