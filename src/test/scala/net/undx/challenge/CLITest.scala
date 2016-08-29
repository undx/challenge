package net.undx.challenge

import java.io.File

import org.scalatest._

/**
  * CLITest 
  * Tests for the CLI object.
  */
class CLITest extends ChallengeTest {

  describe("CLI"){

    it("return None and print usage on --help."){
      CLI.parseCLIOptions(Array("--help")) should be (None)
    }

    it("return None and print usage on empty parameters."){
      CLI.parseCLIOptions(Array()) should be (None)
    }

    it("return None and print a message on erronous file."){
      CLI.parseCLIOptions(Array("-f", "toto.csv")) should be (None)
    }

    it("return a Config object on correct parameters."){
      val cfg = CLI.parseCLIOptions(Array("-f", input))
      cfg should not be (None)
    }

    it("return the same filename in Config than -f parameter."){
      val cfg = CLI.parseCLIOptions(Array("-f", input))
      val fname = cfg match {
        case Some(conf) => conf.filename
        case _ => ""
      }
      fname should be (input)
    }

    it("return the same path as -f parameter if -o option if not defined."){
      val cfg = CLI.parseCLIOptions(Array("-f", input))
      val path = cfg match {
        case Some(conf) => conf.outputPath
        case _ => ""
      }
      path should be (new File(input).getParent)
    }

    it("accept a valid department and reject incorrect department."){
      CLI.validateDepartment("HR1") should be ("HR1")
      CLI.validateDepartment("HR12") should be ("")
    }

    it("accept valid rooms, reject non-valid ones."){
      CLI.validateAndBuildRooms(roomTS0) should be (roomALL)
      CLI.validateAndBuildRooms(roomTS1) should be (Array("roomA"))
      CLI.validateAndBuildRooms(roomTS2) should be (Array("roomA", "roomB", "roomC"))
      CLI.validateAndBuildRooms(roomTS3) should be (Array("roomE"))
    }

    it("return all rooms if all rooms are invalid or empty."){
      CLI.validateAndBuildRooms(roomTS4) should be (roomALL)
      CLI.validateAndBuildRooms(roomTS5) should be (roomALL)
    }
  }
}
