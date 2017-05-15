import ReleaseTransformations._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._
import sbtrelease.ReleasePlugin
import sbt.Keys.scalaVersion
import sbt._

name := "aws_kms_adapter"

organization := "br.com.codesimples"

version := "0.3"

scalaVersion := "2.12.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"

publishMavenStyle := true

sonatypeProfileName := "br.com.codesimples"

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


lazy val commonSettings = ReleasePlugin.extraReleaseCommands ++ Seq(
  organization := "br.com.codesimples",
  scalaVersion := "2.12.2",
  concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  scoverage.ScoverageKeys.coverageMinimum := 96,
  scoverage.ScoverageKeys.coverageFailOnMinimum := false,
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(AlignParameters, true)
    .setPreference(CompactStringConcatenation, false)
    .setPreference(IndentPackageBlocks, true)
    .setPreference(FormatXml, true)
    .setPreference(PreserveSpaceBeforeArguments, false)
    .setPreference(DoubleIndentClassDeclaration, false)
    .setPreference(RewriteArrowSymbols, false)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 40)
    .setPreference(SpaceBeforeColon, false)
    .setPreference(SpaceInsideBrackets, false)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(DanglingCloseParenthesis, Force)
    .setPreference(IndentSpaces, 2)
    .setPreference(IndentLocalDefs, false)
    .setPreference(SpacesWithinPatternBinders, true)
    .setPreference(SpacesAroundMultiImports, true),
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pgpSecretRing := file("local.secring.gpg"),
  pgpPublicRing := file("local.pubring.gpg"),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges
  )
  ,
  pomExtra := (
    <url>https://github.com/agnaldo4j/aws_kms_adapter</url>
      <licenses>
        <license>
          <name>BSD 3-clause "New" or "Revised" License</name>
          <url>https://github.com/agnaldo4j/aws_kms_adapter/blob/develop/LICENSE</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:agnaldo4j/aws_kms_adapter.git</url>
        <connection>scm:git:git@github.com:agnaldo4j/aws_kms_adapter.git</connection>
      </scm>
      <developers>
        <developer>
          <id>agnaldo4j</id>
          <name>Agnaldo de Oliveira</name>
          <url>http://www.agnaldo4j.com.br/</url>
        </developer>
      </developers>)
)