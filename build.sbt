import sbt._

name := "aws_kms_adapter"

organization := "CodeSimples"

version := "0.1"

scalaVersion := "2.12.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"

publishTo := Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots")

credentials += Credentials("Sonatype Nexus Repository Manager", "nexus.scala-tools.org", System.getenv("MAVEN_USER"), System.getenv("MAVEN_PASSWORD"))

lazy val root:Project = Project(id = "root", base = file(".")).
    settings(
        Dependencies.basicConfig: _*
    ).
    settings(
        Dependencies.basicLibraries: _*
    ).dependsOn()
