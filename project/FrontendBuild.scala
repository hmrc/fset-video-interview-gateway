import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object FrontendBuild extends Build with MicroService {

  val appName = "fset-launchpad-gateway"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object Versions {
  val microserviceBootstrapVersion  = "5.14.0"
  val pegdown                       = "1.5.0"
  val jsoup                         = "1.7.3"
  val wiremock                      = "1.57"
  val hmrctest                      = "2.3.0"
  val scalatestplus                 = "1.5.1"
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion
  import Versions._

  private val playHealthVersion = "2.1.0"
  private val playLogbackLoggerVersion = "3.1.0"
  private val frontendBootstrapVersion = "7.19.0"
  private val playUiVersion = "7.0.0"
  private val playPartialsVersion = "5.3.0"
  private val playAuthorisedFrontendVersion = "6.3.0"
  private val playConfigVersion = "4.3.0"
  private val playWhitelist = "2.0.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap"   % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "frontend-bootstrap" % frontendBootstrapVersion,
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-authorised-frontend" % playAuthorisedFrontendVersion,
    "uk.gov.hmrc" %% "play-config" % playConfigVersion,
    "uk.gov.hmrc" %% "logback-json-logger" % playLogbackLoggerVersion,
    "uk.gov.hmrc" %% "play-health" % playHealthVersion,
    "uk.gov.hmrc" %% "play-ui" % playUiVersion,
    "uk.gov.hmrc" %% "play-whitelist-filter" % playWhitelist
  )

  trait TestDependencies {
    lazy val scope: String = "test"
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      lazy val test = Seq(
        "org.mockito" % "mockito-core" % "1.8.5",
        "org.scalatestplus.play"    %% "scalatestplus-play"       % scalatestplus % "test",
        "org.pegdown"               %  "pegdown"                  % pegdown       % "test",
        "org.jsoup"                 %  "jsoup"                    % jsoup         % "test",
        "com.github.tomakehurst"    %  "wiremock"                 % wiremock      % "test",
        "uk.gov.hmrc"               %% "hmrctest"                 % hmrctest      % "test"
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
