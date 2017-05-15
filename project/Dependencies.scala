import sbt.Keys._
import sbt._

object Dependencies {
  lazy val jacksonVersion = "2.8.5"
  lazy val specsVersion = "3.8.9"
  lazy val awsSdkVersion = "1.11.128"

  val basicResolvers = Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"
  )

  lazy val basicConfig = Seq(
    scalacOptions in Test ++= Seq("-Yrangepos"),

    parallelExecution in Test := false,

    resolvers ++= basicResolvers,

    unmanagedBase := file("./lib")
  )

  lazy val basicLibraries = Seq(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % specsVersion % "test",
      "org.specs2" %% "specs2-junit" % specsVersion % "test",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
      "ch.qos.logback" % "logback-classic" % "1.1.7"
    )
  )

  lazy val bouncycastleLibraries = Seq(
    libraryDependencies ++= Seq(
      "org.bouncycastle" % "bcprov-ext-jdk15on" % "1.56"
    )
  )

  lazy val awsEncryptionSdkLibraries = Seq(
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk-core" % awsSdkVersion,
      "com.amazonaws" % "aws-java-sdk-kms" % awsSdkVersion,
      "com.amazonaws" % "aws-encryption-sdk-java" % "0.0.1"
    )
  )

  lazy val springLibraries = Seq(
    libraryDependencies ++= Seq(
      "org.springframework" % "spring-context" % "4.3.1.RELEASE"
    )
  )

  lazy val jacksonDependencies = Seq(
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion
    )
  )
}