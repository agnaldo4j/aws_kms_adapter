import sbt._

name := "aws_kms_adapter"

organization := "br.com.codesimples"

version := "0.3"

scalaVersion := "2.12.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  //if (isSnapshot.value)
  //  Some("snapshots" at nexus + "content/repositories/snapshots")
  //else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", System.getenv("MAVEN_USER"), System.getenv("MAVEN_PASSWORD"))

lazy val root:Project = Project(id = "root", base = file(".")).
    settings(
        Dependencies.basicConfig: _*
    ).
    settings(
        Dependencies.basicLibraries: _*
    ).
    settings(
      Dependencies.bouncycastleLibraries: _*
    ).
    settings(
      Dependencies.awsEncryptionSdkLibraries: _*
    ).dependsOn()
