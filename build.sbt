
name := "feku"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq (
  "com.typesafe.akka" %% "akka-actor" % "2.5.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.2" % "test",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.2",
  "commons-cli" % "commons-cli" % "1.4",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "org.json4s" %% "json4s-native" % "3.5.2"
)

mainClass in (Compile, run) := Some("trainer.Main")

mainClass in (Compile, packageBin) := Some("trainer.Main")

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.mishrapaw",
  scalaVersion := "2.12.2",
  test in assembly := {}
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    assemblyJarName in assembly := "trainer-0.1-SNAPSHOT.jar",
    mainClass in assembly := Some("trainer.Main"),
    target in assembly := file("./build/")
  )