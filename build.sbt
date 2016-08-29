import com.github.retronym.SbtOneJar._

name := "Challenge" // our project's name,
version := "1.0.0" // its release
scalaVersion := "2.11.8" // and is using scala in 2.11 version.

// include oneJar settings
oneJarSettings

// Other tasks are in the project/Build.scala file.

// PROJECT DEPENDENCIES
resolvers += Resolver.sonatypeRepo("public")
// for one-jar sbt plugin
libraryDependencies += "commons-lang" % "commons-lang" % "2.6"
// Tests framework
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"
// Command-line parser
libraryDependencies += "com.github.scopt" %% "scopt" % "3.5.0"
// CSV parser 
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.1.13"
// jackson-csv engine.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-jackson" % "0.1.13"
