package net.undx.challenge

import java.io.{File, FileWriter}

import scala.collection.mutable.HashMap
import scala.io.Source

import kantan.csv.ops._     // kantan.csv syntax
import kantan.csv.generic._ // case class decoder derivation
import kantan.codecs.Result._ // for safe methods (Success, Failure)

/** Worker custom exception for invalid structure in input file. */
case class WorkerException(msg: String) extends Exception(msg)

/**
  *  Represent a row of the input file according the specifications.
  */
case class InputRow(
  department: String,
  date: String,
  roomA: Option[Int], roomB: Option[Int], roomC: Option[Int],
  roomD: Option[Int], roomE: Option[Int], roomF: Option[Int],
  code: Option[String]) {
  /** convert to a Map */
  def toMap = {
    getClass.getDeclaredFields
      .map(_.getName)
      .zip(productIterator.to)
      .toMap
  }
}

/**
  * Singleton to parse, process the input CSV file and generate the outputs.
  *
  */
object Worker {
  /**
    * Read and check `filename` and write the relevant output files.
    * @throws WorkerException on invalid structure in input file .
    * @return Unit.
    * @param file the input file to process.
    * @param departmentFilter the department to filter. Otherwise, all departements.
    * @param rooms the room selection to output.
    * @param path the path where to write generated files.
    */  
  def process(file: String, departmentFilter: String = "", rooms: Seq[String], path: String) = {
    // writers: memoized output file writers (10 departments * 6 rooms -> 60 writers max).
    var writers:HashMap[String, kantan.csv.CsvWriter[(String, String, Int, String, String)]] = HashMap()
    val start = System.currentTimeMillis
    // first, we check input file structure integrity against the schema.
    var errors = new File(file).asCsvReader[InputRow](';', true).collect { case Failure(f) => f }
    if (errors.hasNext) {
      throw new WorkerException("Invalid CSV strucure in input file !")
    }
    Console.out.println(s"Structure integrity check performed in ${((System.currentTimeMillis) - start)} ms.")
    // iterator is consummed, create a new one on valid rows.
    var rows = new File(file).asCsvReader[InputRow](';', true).collect { case Success(s) => s }
    // it specified, we filter the rows on the deparment.
    if (!departmentFilter.isEmpty) { rows = rows.filter(_.department == departmentFilter) }
    // initially, we looped like this : `for (row <- rows; room <- rooms) { // iterate on row and rooms`
    // but `code` would have been assigned up to 6 times...
    for (row <- rows) {
      val code = row.code match{
        case Some(c) => c.split("#.*#")
        case None => Array("", "")
      }
      for (room <- rooms) {
        // according to the current room's value.
        row.toMap(room) match {
          case None => { }
          case Some(count: Int) => {
            val key = s"${row.department}-${room.capitalize}"
            val file = s"${path}${File.separator}${key}.csv"
            // get a writer for room or create it.
            val writer = writers.getOrElse(key, None) match {
              // we create the writer for the deparment/room
              case None  => {
                val w = (new FileWriter(file, true)).asCsvWriter[(String, String, Int, String, String)](';')
                writers(key) = w
                w
              }
              // already created, return it
              case w => { w.asInstanceOf[kantan.csv.CsvWriter[(String, String, Int, String, String)]] }
            }
            writer.write(row.department, row.date, count, code(0), code(1))
          }
        }
      }
    }
    // close all writers
    writers.map(w => w._2.close())
    Console.out.println(s"File processed in ${((System.currentTimeMillis) - start)} ms.")
  }
}
