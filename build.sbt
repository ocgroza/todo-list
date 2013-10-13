import play.Project._

name := """project-arseniy-zhizhelev"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.0", 
  "org.webjars" % "bootstrap" % "2.3.1",
  jdbc,
  anorm,
  cache
) 

playScalaSettings
