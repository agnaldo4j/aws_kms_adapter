import sbt._

name := "aws_kms_adapter"

organization := "br.com.codesimples"

version := "0.4"

scalaVersion := "2.12.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"

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