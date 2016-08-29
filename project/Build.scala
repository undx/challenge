import java.util.Calendar

import sbt._

/** 
  * sbt(simple-build-tool) project's build extensions.
  * Used to create new tasks where the code is too long to fit in build.sbt
  */
object MyBuild extends Build {

  /** Our custom task to generate data samples */
  lazy val generateDatasets = taskKey[Unit]("Generates sample datasets in the data folder")
  lazy val root = Project(id = "Challenge", base = file("."))
    .settings( generateDatasets := {
      val folder = "./data/"
      println(s"Generating the sample datasets in $folder folder.")
      if (! new java.io.File(folder).exists) { new File(folder).mkdir() }
      Array(
        ("tiny.csv"       , 100),
        ("small.csv"      , 1000),     // 1k
        ("medium.csv"     , 100000),   // 100k
        ("large.csv"      , 1000000),  // 1M
        ("huge.csv"       , 10000000) // 10M
      ).map(c => generateDataset(s"${folder}${c._1}", c._2))
      println("Done.")
    })

  /** Generates a random date (YYYY-MM-DD) in an arbitrary range of year (2009 to 2016).
    * Inspired by the stackoverflow.org topic 'Generate random date of birth'
    * http://stackoverflow.com/questions/3985392/generate-random-date-of-birth
    */
  def getRandomDate: String = {
    val rnd = util.Random
    val dateRange = 2009 to 2016
    val cal = Calendar.getInstance()
    val year = dateRange(rnd.nextInt(dateRange.size));
    cal.set(Calendar.YEAR, year);
    val dayOfYear = rnd.nextInt(cal.getActualMaximum(Calendar.DAY_OF_YEAR)+1);
    cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
    val result = f"${cal.get(Calendar.YEAR)}-${(cal.get(Calendar.MONTH) + 1)}%02d-${cal.get(Calendar.DAY_OF_MONTH)}%02d"
    result
  }

  /** Generates a random row (see specifications in documentation).
    * Everything is generated randomly in the specifications sets.
    *  @param filename the file to write data.
    *  @param rows the number of rows to generate.
    */
  def generateDataset(filename: String, rows: Int) = {
    val rnd = util.Random
    val departments = for (d <- 1 to 10) yield s"HR${d}"
    val rooms = for (r <- 'A' to 'F') yield s"room$r"
    val refs = Array("HALO", "GEPUR", "TEPUR", "RIRI")
    val curs = Array("EUR", "GBP", "OPEC", "OPEX")
    // display filename processed
    print(f"$filename%-17s ")
    val writer = new java.io.PrintWriter(filename)
    // header
    writer.write("department;date;roomA;roomB;roomC;roomD;roomE;roomF;code\n")
    // rows
    for (n <- 1 to rows) {
      val department = departments(rnd.nextInt(departments.size))
      val date = getRandomDate
      writer.write(s"${department};")
      writer.write(s"${date};")
      for (r <- rooms) {
        // 5% of empty rooms
        if (rnd.nextInt(100) > 5) { 
          writer.write(s"${rnd.nextInt(130)+1}")
        }
        writer.write(";")
      }
      // (Empty or not) code field
      if (rnd.nextBoolean()) {
        writer.write(refs(rnd.nextInt(refs.size)))
        writer.write("#"+ rnd.alphanumeric.take(3).mkString+"#")
        writer.write(curs(rnd.nextInt(curs.size)))
      }
      writer.write("\n")
      // progress display (step by 10%)
      if (n % (rows *0.1).toInt == 0) { print(".") }
    }
    // close line and file
    println
    writer.close()
  }
}
