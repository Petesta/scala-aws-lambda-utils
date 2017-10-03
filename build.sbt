name := "scala-aws-lambda-utils"

organization := "io.github.petesta"

version := "0.0.1"

scalaVersion := "2.11.11"

description := "AWS Lambda utils"

val aws = "com.amazonaws"
val awsVersion = "1.1.0"
val circe = "io.circe"
val circeVersion = "0.8.0"

libraryDependencies ++= Seq(
  aws % "aws-lambda-java-core" % awsVersion,
  aws % "aws-lambda-java-events" % awsVersion % Test,
  circe %% "circe-core" % circeVersion,
  circe %% "circe-generic" % circeVersion,
  circe %% "circe-java8" % circeVersion,
  circe %% "circe-parser" % circeVersion,
  "org.mockito" % "mockito-core" % "2.10.0" % Test,
  "org.scalatest" %% "scalatest" % "3.0.2" % Test
)

val yWarnUnusedImport = "-Ywarn-unused-import"
val yWarnValueDiscard = "-Ywarn-value-discard"

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  yWarnUnusedImport,
  yWarnValueDiscard,
  "-encoding", "UTF-8",
  "-feature",
  "-deprecation",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked"
)

scalacOptions in (Compile, console) ~= { _.filterNot(Set(yWarnUnusedImport, yWarnValueDiscard)) }

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra in Global := {
  <url>https://github.com/Petesta/scala-aws-lambda-utils</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:Petesta/scala-aws-lambda-utils.git</url>
    <connection>scm:git:git@github.com:Petesta/scala-aws-lambda-utils.git</connection>
  </scm>
  <developers>
    <developer>
      <id>Petesta</id>
      <name>Pete Cruz</name>
      <url>https://github.com/Petesta/</url>
    </developer>
  </developers>
}
