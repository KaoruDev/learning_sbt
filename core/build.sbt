import scala.sys.process.Process

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
val gigahorse = "com.eed3si9n" %% "gigahorse-okhttp" % "0.3.1"
val playJson  = "com.typesafe.play" %% "play-json" % "2.6.9"

version := "0.2.1"
name := "The Real Core name"
libraryDependencies += scalaTest
libraryDependencies ++= Seq(gigahorse, playJson)

val gitHeadCommitSha = taskKey[String]("Determines the current git commit SHA")
gitHeadCommitSha in ThisBuild := Process("git rev-parse HEAD").lineStream.head

val makePropertiesFile = taskKey[Seq[File]]("Makes a version.properties file.")

makePropertiesFile := {
  // resourcedManaged in Compile defines where to write the new file to
  // target/scala-*/resource_managed/main/version.properties
  val propFile = new File((resourceManaged in Compile). value, "version.properties")

  val content = "version=%s" format gitHeadCommitSha.value

  IO.write(propFile, content)

  // Returns the files created, cause the taskKey want it
  Seq(propFile)
}
