import play.sbt.PlayImport.ws
import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object FrontendBuild extends Build with MicroService {
  val appName = "fset-video-interview-gateway"
  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object Versions {
  val hmrcFrontendBootstrapVersion      = "2.25.0"
  val hmrcMicroserviceBootstrapVersion  = "2.25.0"
  val playJsonJodaVersion               = "2.6.10"

  val hmrctestVersion                   = "3.0.0"
  val scalatestplusVersion              = "3.1.3"
  val mockitoAllVersion                 = "1.10.19"
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import Versions._

  val compile = Seq(
    ws,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-26"    % hmrcMicroserviceBootstrapVersion,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-26"   % hmrcFrontendBootstrapVersion,
    "com.typesafe.play" %% "play-json-joda"               % playJsonJodaVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      lazy val test = Seq(
        "org.mockito"               %  "mockito-all"              % mockitoAllVersion,
        "org.scalatestplus.play"    %% "scalatestplus-play"       % scalatestplusVersion  % "test",
        "uk.gov.hmrc"               %% "hmrctest"                 % hmrctestVersion       % "test",
        "uk.gov.hmrc"               %% "bootstrap-test-play-26"       % "2.24.0",
        "com.github.tomakehurst"    %  "wiremock-jre8"                     % "2.27.2"        % "test"
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
