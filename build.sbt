ThisBuild / scalaVersion := "2.12.7"
ThisBuild / version := "0.3.0"
ThisBuild / organization := "com.example"
ThisBuild / sbtVersion := "1.2.4"

lazy val helloTwo = (project in file("hellotwo"))
  .settings(
    name := "Hello Two"
  )

lazy val helloCore = (project in file("core"))
  .dependsOn(helloTwo)
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

val myTask = taskKey[Unit]("my task woot")

myTask := {
  println(subProjects.map({
    case (projectId: String, project: Project) => (projectId, project.dependencies.map(_.project).foreach({
      case localProject: LocalProject => localProject.project match {
        case "helloTwo" => (helloTwo / Compile / compile).value
        case "helloCore" => (helloCore / Compile / compile).value
        case projectName => println(s"KODU: $projectName")
      }
      case _ => ()
    }))
  }))
}

// Not really sure what this does...
//resourceGenerators in Compile += makePropertiesFile

lazy val subProjects = Seq(hello, helloCore, helloTwo).map(p => p.id -> p).toMap

lazy val main = (project in file ("."))
  .aggregate(subProjects.values.toSeq.map(Project.projectToLocalProject):_*)
  .settings(
    publish / aggregate := false,
  )
