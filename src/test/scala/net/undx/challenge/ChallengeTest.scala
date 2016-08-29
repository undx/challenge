package net.undx.challenge

import java.io.File

import org.scalatest._

/**
  * ChallengeTest 
  * Test class for sharing methods, fixtures, etc.
  */
class ChallengeTest extends FunSpec with Matchers {

  val roomALL = Array("A", "B", "C", "D", "E", "F").map { "room" + _ }
  val roomTS0 = Array("A", "B", "C", "D", "E", "F")
  val roomTS1 = Array("A", "X", "Z")
  val roomTS2 = Array("A", "B", "C")
  val roomTS3 = Array("E", "VCVCB", "12374C")
  val roomTS4 = Array("Z", "VCVCB", "12374C") // none valid
  val roomTS5: Array[String] = Array()
  // values in the challenge definition
  val resultHR3RoomA = Array("HR3;2009-01-20;19;HALO;OPEC","HR3;2009-01-21;21;;")
  val resultHR3RoomB = Array("HR3;2009-01-20;4;HALO;OPEC","HR3;2009-01-19;5;HALO;EUR","HR3;2009-01-21;89;;")
  val resultHR3RoomC = Array("HR3;2009-01-20;3;HALO;OPEC","HR3;2009-01-19;3;HALO;EUR","HR3;2009-01-21;56;;")

  val input = getClass.getResource("/input.csv").getFile
  val folder = getClass.getResource("/").getFile
  val invalid = getClass.getResource("/invalid.csv").getFile

  // cleanup folder for tests consistancy
  def cleanupTestFiles() = {
    for (del <- 'A' to 'F') {
      new File(s"${folder}HR3-Room${del}.csv").delete()
    }
  }
}
