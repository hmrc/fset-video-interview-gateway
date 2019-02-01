import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object FrontendBuild extends Build with MicroService {

  val appName = "fset-video-interview-gateway"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object Versions {
  val hmrcFrontendBootstrapVersion      = "11.3.0"
//  val hmrcFrontendBootstrapVersion      = "10.4.0"
  val hmrcMicroserviceBootstrapVersion  = "9.1.0"
//  val hmrcMicroserviceBootstrapVersion  = "8.3.0"
  val hmrcPlayPartialsVersion           = "6.1.0"
  val hmrcPlayAuthorisedFrontendVersion = "7.1.0"
//  val hmrcPlayAuthorisedFrontendVersion = "7.0.0"
  val hmrcPlayWhitelistVersion          = "2.0.0"

  val hmrctest                          = "2.4.0"
  val scalatestplus                     = "1.5.1"
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import Versions._

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap"   % hmrcMicroserviceBootstrapVersion,
    "uk.gov.hmrc" %% "frontend-bootstrap"       % hmrcFrontendBootstrapVersion,
    "uk.gov.hmrc" %% "play-partials"            % hmrcPlayPartialsVersion,
    "uk.gov.hmrc" %% "play-authorised-frontend" % hmrcPlayAuthorisedFrontendVersion,
    "uk.gov.hmrc" %% "play-whitelist-filter"    % hmrcPlayWhitelistVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      lazy val test = Seq(
        "org.mockito" % "mockito-all" % "1.10.19",
        "org.scalatestplus.play"    %% "scalatestplus-play"       % scalatestplus % "test",
        "uk.gov.hmrc"               %% "hmrctest"                 % hmrctest      % "test"
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
