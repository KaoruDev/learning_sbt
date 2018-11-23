

ThisBuild / scalaVersion := "2.12.7"
ThisBuild / version := "0.3.0"
ThisBuild / organization := "com.example"
ThisBuild / sbtVersion := "1.2.4"

lazy val helloCore = (project in file("core"))
  .settings(
    name := "Hello Core"
  )

lazy val hello = (project in file("hello"))
//  .aggregate(helloCore) // makes it possible for some commands executed in hello to be echoed to helloCore, i.e test
  .dependsOn(helloCore)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "Hello",
  )

// Not really sure what this does...
//resourceGenerators in Compile += makePropertiesFile

lazy val main = (project in file ("."))
  .aggregate(hello, helloCore)
  .settings(
    publish / aggregate := false,
  )
