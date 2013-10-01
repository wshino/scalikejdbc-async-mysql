import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "scalikejdbc-async"
  val appVersion      = "1.0-SNAPSHOT"
  lazy val scalikejdbcVersion = "1.6.8"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.github.seratch"  %% "scalikejdbc-async" % "[0.2,)",
    "com.github.seratch"  %% "scalikejdbc-async-play-plugin" % "[0.2,)",
    "org.slf4j"           %  "slf4j-simple"      % "[1.7,)",
    "com.github.mauricio" %% "mysql-async"               % "[0.2,)"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion in ThisBuild := "2.10.2",
    resolvers ++= Seq(
      "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases",
      "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
    )
  )

}
