package net.undx.challenge

import java.io.File

import org.scalatest._

/**
  * WorkerTest 
  * Tests for Worker object.
  */
class WorkerTest extends ChallengeTest {

  describe("Worker") {

    it("throw an exception on non-existant filename path.") {
      assertThrows[Exception] {
        Worker.process("tator.csv", "", Seq("roomA", "roomB"), "")
      }
    }

    it("throw an exception on invalid structure in input file.") {
      assertThrows[WorkerException] {
        Worker.process(invalid, "", Seq("roomA", "roomB"), "")
      }
    }

    it("return the correct values for HR3, RoomA.") {
      cleanupTestFiles
      Worker.process(input, "HR3", Seq("roomA"), folder)
      io.Source.fromFile(folder+"HR3-RoomA.csv").getLines.toArray should be (resultHR3RoomA)
    }

    it("return the correct values for HR3, RoomB.") {
      Worker.process(input, "HR3", Seq("roomB"), folder)
      io.Source.fromFile(folder+"HR3-RoomB.csv").getLines.toArray should be (resultHR3RoomB)
    }

    it("return the correct values for HR3, RoomC.") {
      Worker.process(input, "HR3", Seq("roomC"), folder)
      io.Source.fromFile(folder+"HR3-RoomC.csv").getLines.toArray should be (resultHR3RoomC)
    }

    it("create all HR3 rooms files with the HR3 department filter."){
      cleanupTestFiles
      Worker.process(input, "HR3", roomALL, folder)
      // all files should be created...
      val existance = (
        for (r <- 'A' to 'F') yield (new File(folder+s"HR3-Room${r}.csv").exists)
      ).foldLeft(true) {(acc, ex) =>  acc && ex }

      existance should be (true)
      // final cleanup
      cleanupTestFiles
    }
  }
}
