import scala.sys.process.Process

ThisBuild / scalaVersion := "2.12.7"
ThisBuild / version := "0.3.0"
ThisBuild / organization := "com.example"
ThisBuild / sbtVersion := "1.2.4"

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
val gigahorse = "com.eed3si9n" %% "gigahorse-okhttp" % "0.3.1"
val playJson  = "com.typesafe.play" %% "play-json" % "2.6.9"

val gitHeadCommitSha = taskKey[String]("Determines the current git commit SHA")
gitHeadCommitSha in ThisBuild := Process("git rev-parse HEAD").lineStream.head

val makePropertiesFile = taskKey[Seq[File]]("Makes a version.properties file.")

lazy val helloCore = (project in file("core"))
  .settings(
    name := "Hello Core",
    libraryDependencies += scalaTest,
    libraryDependencies ++= Seq(gigahorse, playJson),
    makePropertiesFile := {
      // resourcedManaged in Compile defines where to write the new file to
      // target/scala-*/resource_managed/main/version.properties
      val propFile = new File((resourceManaged in Compile). value, "version.properties")

      val content = "version=%s" format gitHeadCommitSha.value

      IO.write(propFile, content)

      // Returns the files created, cause the taskKey want it
      Seq(propFile)
    },
  )

lazy val myTask = taskKey[Unit]("some tasking thing")

lazy val hello = (project in file("."))
  .aggregate(helloCore) // makes it possible for some commands executed in hello to be echoed to helloCore, i.e test
  .dependsOn(helloCore)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "Hello",
    libraryDependencies += scalaTest,
  )

// Not really sure what this does...
//resourceGenerators in Compile += makePropertiesFile
