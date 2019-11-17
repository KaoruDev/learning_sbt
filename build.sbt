ThisBuild / scalaVersion := "2.12.8" // Global Scala version
ThisBuild / version := "0.3.1" // Project version
ThisBuild / organization := "com.kaoruk"
ThisBuild / sbtVersion := "1.3.0" // Global SBT version

lazy val helloCore = project.in(file("core"))
    .settings(
      name := "Hello Core"
    )

lazy val hello = project.in(file("hello"))
  .settings(
    name := "Hello"
  )
  .dependsOn(helloCore)

resolvers += DefaultMavenRepository