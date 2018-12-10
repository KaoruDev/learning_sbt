import sbt.Keys._
import sbt._

object Subprojects {
  val helloTwo = SubProject("helloTwo", "helloTwo")
    .settings(
      name := "Hello Two"
    ).build()

  val helloCore = SubProject("core", "core")
    .settings(
      name := "Hello Core",
    )
    .addDep(helloTwo)
    .build()

  val hello = SubProject("hello", "hello")
    .settings(
      name := "Hello",
    )
    .addDep(helloCore)
    .build()
}

case class SubProject(name: String,
                      projectPath: String,
                      dependencies: Seq[SubProject] = Seq(),
                      ref: Option[Project] = None,
                      settings: Seq[SettingsDefinition] = Seq(),
                      private var upstreamDependencies: Seq[SubProject] = Seq()) {

  def addDep(newProjects: SubProject*): SubProject = {
    println(s"$name adding dependency")
    newProjects.foreach(_.addUpstream(this))
    copy(dependencies = newProjects)
  }

  def settings(newSettings: SettingsDefinition*): SubProject = {
    println(s"$name adding setting")
    copy(newSettings = newSettings)
  }

  def project: Project = ref.get

  def build(): SubProject = {
    if (ref.isDefined) return this

    val project = Project(name, file(projectPath))
      .settings(this.settings: _*)
      .dependsOn(dependencies.map(project => {
        classpathDependency(project.build().project)(Project.projectToLocalProject)
      }): _*)

//    println(s"$name's upstream: ${upStreamDependencies.map(_.name)}, ref is: ${ref}")

    println(s"$name: building")
    copy(projectRef = Some(project))
  }

  def getAllUpstream(): Seq[SubProject] = {
    println(s"($name) <- ${upstreamDependencies.map(_.name)}")
    upstreamDependencies ++ upstreamDependencies.flatMap(_.getAllUpstream())
  }

  private def copy(dependencies: Seq[SubProject] = dependencies,
                   projectRef: Option[Project] = None,
                   newSettings: Seq[SettingsDefinition] = Seq(),
                   upstream: Seq[SubProject] = Seq()): SubProject = {
    println(s"Copying upstream for $name : ${upstreamDependencies.map(_.name)}")

    SubProject(
      name,
      projectPath,
      this.dependencies ++ dependencies,
      ref.orElse(projectRef),
      this.settings ++ newSettings,
      this.upstreamDependencies
    )
  }

  private def addUpstream(upstreamProject: SubProject): Unit = {
    println(s"${upstreamProject.name} -> $name")
    upstreamDependencies = upstreamDependencies :+ upstreamProject
  }
}
