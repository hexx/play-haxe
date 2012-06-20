sbtPlugin := true

name := "play-haxe"

organization := "com.github.hexx"

version := "0.0.1"

scalacOptions := Seq("-deprecation", "-unchecked")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies <++= (scalaVersion, sbtVersion) { (scalaV, sbtV) => Seq(
  "play" % "sbt-plugin" % "2.0.1" extra("scalaVersion" -> scalaV, "sbtVersion" -> sbtV)
)}
