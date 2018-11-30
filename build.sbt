ThisBuild / scalaVersion := "2.12.7" // Global Scala version
ThisBuild / version := "0.3.0" // Project version
ThisBuild / organization := "com.kaoruk"
ThisBuild / sbtVersion := "1.2.4" // Global SBT version

lazy val someVal = taskKey[String]("Some value")

// by default SBT will assign values to a project's scope, in this case it would be scoped to project named
// main (see def below). Add in Global specifically adds the value to the Global scope allowing for projects
// to access the value.
Global / someVal := "global value"

// This is also acceptable
ThisBuild / someVal := "This build's val"

lazy val echo = taskKey[Unit]("print value of someVal")
echo := {
  println(someVal.value)
}

lazy val helloTwo = (project in file("hellotwo"))
  .settings(
    name := "Hello Two",
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
    someVal := {
      s"someVal from ThisBuild: ${(someVal in ThisBuild).value} and someVal from Global: ${(someVal in Global).value}"
    }
  )

lazy val myTask = taskKey[Unit]("my task woot")

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
lazy val main = (project in file ("."))
  .aggregate(subProjects.values.toSeq.map(Project.projectToLocalProject):_*)
  .settings(
    publish / aggregate := false,
  )

lazy val subProjects = Seq(hello, helloCore, helloTwo).map(p => p.id -> p).toMap


// In the example below, task C only runs once

lazy val taskA = taskKey[String]("Task A")
taskA := {
  println("running task A")
  s"Value of task B${taskB.value} : Value of task C ${taskC.value}"
}

lazy val taskB = taskKey[String]("Task B")

taskB := {
  println("running task B")
  taskC.value
}

lazy val taskC = taskKey[String]("Task C")
taskC := {
  println("Running task C")
  "C"
}

// When a task throws an exception, the calling task stops where the error was thrown and the SBT command exits with a 1
// but note because of the special way taskKey.value works, all downstream tasks will be called even if a task prior
// throws an exception, that's because tasks are called independent of each other.
lazy val taskExA = taskKey[String]("Task Exception A")
taskExA := {
  println("running task A")
  s"Value of task B${taskExB.value} : Value of task C ${taskExC.value}"
  println("Does this still run?")
  "foobar"
}

lazy val taskExB = taskKey[String]("Task Exception B")

taskExB := {
  println("Running task B")
  throw new IllegalStateException("ERRROR")
}

lazy val taskExC = taskKey[String]("Task Exception C")
taskExC := {
  println("Running task C")
  "C"
}

// You can't set the value of a taskKey to a settingsKey but what about a method output? Works!
def foobar(): String = {
  println("running foobar what!?")
  "nomz"
}

val methodSetKey = settingKey[String]("cached key")
ThisBuild / methodSetKey := foobar()

val methodTaskKey = taskKey[String]("cached task key")
methodTaskKey := { foobar() }

// what if we just defined a bunch of methods, will ordering be maintained?
def meThrow(): Unit = throw new IllegalArgumentException("I throw what!?1")

val defThrowing = taskKey[String]("throwing methods")

// Can't call a task within a method =(
//def taskInMethod(): String = taskA()

defThrowing := {
  meThrow()
  foobar() // this does not run
}

