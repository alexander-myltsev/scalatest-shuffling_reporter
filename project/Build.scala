import sbt._
import sbt.Keys._

object Build extends Build {
  lazy val SortedTest = config("sorted") extend (Test)
  
  val commonSettings = Seq(
    scalacOptions ++= Seq(
      "-deprecation"
    ),
    scalaVersion := "2.11.2"
  )

  lazy val shuffler = Project("shuffler", file("."))
    .configs(SortedTest)
    .settings(inConfig(SortedTest)(Defaults.testTasks): _*)
    .settings(
      name := "scalatest-shuffling_reporter",
      organization := "name.myltsev",
      version := "0.1",

      testOptions in SortedTest := Seq(
        Tests.Argument("-C", "name.myltsev.SortingReporter")
      ),

      resolvers ++= Seq(
        "twitter.com" at "http://maven.twttr.com/"
      ),
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.2.0",
        "joda-time" % "joda-time" % "2.3"
      )
    )
}
