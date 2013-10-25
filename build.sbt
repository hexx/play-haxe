sbtPlugin := true

name := "play-haxe"

organization := "com.github.hexx"

version := "0.0.1"

scalaVersion := "2.10.3"

scalacOptions := Seq("-deprecation", "-unchecked")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.0")

publishMavenStyle := true

publishArtifact in Test := false

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) 
    Some("snapshots" at nexus + "content/repositories/snapshots") 
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/hexx/play-haxe</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:hexx/play-haxe.git</url>
    <connection>scm:git:git@github.com:hexx/play-haxe.git</connection>
  </scm>
  <developers>
    <developer>
      <id>hexx</id>
      <name>Seitaro Yuuki</name>
      <url>https://github.com/hexx</url>
    </developer>
  </developers>)
