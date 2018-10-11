import com.typesafe.sbt.SbtScalariform.{ScalariformKeys, _}
import com.typesafe.sbt.web.Import._
import play.routes.compiler.StaticRoutesGenerator
import sbt.Keys._
import sbt.Tests.{Group, SubProcess}
import sbt._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import play.sbt.routes.RoutesKeys.routesGenerator
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

trait MicroService {

  import uk.gov.hmrc._
  import DefaultBuildSettings.{scalaSettings,defaultSettings,targetJvm,addTestReportOption}
  import uk.gov.hmrc.{SbtBuildInfo, ShellPrompt, SbtAutoBuildPlugin}
  import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
  import uk.gov.hmrc.versioning.SbtGitVersioning

  import TestPhases._
  import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
  import scalariform.formatter.preferences._

  val appName: String

  val appDependencies : Seq[ModuleID]
  lazy val plugins : Seq[Plugins] = Seq(SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  lazy val playSettings : Seq[Setting[_]] = Seq.empty

  lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

  lazy val microservice = Project(appName, file("."))
    .enablePlugins(Seq(play.sbt.PlayScala,SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin) ++ plugins : _*)
    .settings(majorVersion := 0)
    .settings(playSettings : _*)
    .settings(scalaSettings: _*)
    .settings(publishingSettings: _*)
    .settings(defaultSettings(): _*)
    .settings(
      targetJvm := "jvm-1.8",
      scalaVersion := "2.11.11",
      libraryDependencies ++= appDependencies,
      routesGenerator := StaticRoutesGenerator,
      parallelExecution in Test := false,
      fork in Test := false,
      retrieveManaged := true,
      evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
      scalacOptions += "-feature",
      // Currently don't enable warning in value discard in tests until ScalaTest 3
      scalacOptions in (Compile, compile) += "-Ywarn-value-discard"
    )
    .configs(IntegrationTest)
    .settings(inConfig(IntegrationTest)(Defaults.testSettings) : _*)
    .settings(scalariformSettings: _*)
    .settings(ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(FormatXml, false)
      .setPreference(DoubleIndentClassDeclaration, false)
      .setPreference(DanglingCloseParenthesis, Preserve))
    .settings(compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value,
      (compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle)
    .settings(
      Keys.fork in IntegrationTest := false,
      unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest)(base => Seq(base / "it")),
      addTestReportOption(IntegrationTest, "int-test-reports"),
      testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
      parallelExecution in IntegrationTest := false)
    .settings(resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo
    ))
    .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
}

private object TestPhases {

  def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] =
    tests map {
      test => Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
    }
}
