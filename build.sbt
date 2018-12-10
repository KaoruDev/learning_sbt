ThisBuild / scalaVersion := "2.12.7" // Global Scala version
ThisBuild / version := "0.3.0" // Project version
ThisBuild / organization := "com.kaoruk"
ThisBuild / sbtVersion := "1.2.4" // Global SBT version

lazy val someVal = settingKey[String]("Some value")

// by default SBT will assign values to a project's scope, in this case it would be scoped to project named
// main (see def below). Add in Global specifically adds the value to the Global scope allowing for projects
// to access the value.
Global / someVal := "global value"

// This is also acceptable, but puts it in a separate scope
ThisBuild / someVal := "This build's val"

lazy val echo = taskKey[Unit]("print value of someVal")
echo := {
  println(s"Global: ${(Global / someVal).value}")
  println(s"ThisBuild: ${(ThisBuild / someVal).value}")
}

lazy val helloTwo: Project = Subprojects.helloTwo.project
lazy val helloCore = Subprojects.helloCore.project
lazy val hello = Subprojects.hello.project
  .settings(
    someVal := {
      s"someVal from ThisBuild: ${(someVal in ThisBuild).value} and someVal from Global: ${(someVal in Global).value}"
    }
  )

// Plugins delegate down to all projects
lazy val main = (project in file ("."))
  .enablePlugins(SbtPlugin, HelloPlugin)
  .aggregate(
    helloCore,
    hello,
    helloTwo,
  )
  .settings(
    publish / aggregate := false,
  )

// In the example below, task C only runs once

lazy val taskA = taskKey[String]("Task A")
taskA := {
  println("running task A")
  s"Value of task B${taskB.value} : Value of task C ${taskC.value} and.... to be..."
}

taskA += {
  println("Yet another one")
  " continued!!"
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
// By defining this task to Global, all subprojects will run this
Global / taskExC := {
  println("Running task C")
  // Task definition creates a closure, thus ThisProject refers to main
  println(s"Project name: ${(ThisProject / name).value}")
  "C"
}

lazy val taskExD = taskKey[String]("Task Exception D")
Global / taskExD := Def.taskDyn[String] {
  // Even dynamic definitions will enclose on the current scope. ThisProject refers to main
  val project = ThisProject
  Def.task {
    println(s"TaskD project name: ${(project / name).value}")
    (project / name).value
  }
}.value

// You can't set the value of a taskKey to a settingsKey but what about a method output? Works!
def foobar(): String = {
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


lazy val listDeps = taskKey[List[String]]("List dependencies")
listDeps := {
  Subprojects.helloTwo.getAllUpstream().map(_.name).toList
}

lazy val listCoreDeps = taskKey[List[String]]("List dependencies")
listCoreDeps := {
  Subprojects.helloCore.getAllUpstream().map(_.name).toList
}
