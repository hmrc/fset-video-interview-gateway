import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object FrontendBuild extends Build with MicroService {

  val appName = "fset-launchpad-gateway"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object Versions {
  val microserviceBootstrapVersion  = "4.2.0"
  val ficus                         = "1.1.2"
  val cacheClient                   = "5.3.0"
  val frontend                      = "6.4.0"
  val playConfig                    = "2.0.1"
  val playHealth                    = "1.1.0"
  val urlBuilder                    = "1.0.0"
  val httpclient                    = "4.3.4"
  val jsonLogger                    = "2.1.1"
  val passcode                      = "3.2.0"
  val scalatest                     = "2.2.2"
  val pegdown                       = "1.4.2"
  val jsoup                         = "1.7.3"
  val wiremock                      = "1.57"
  val hmrctest                      = "1.4.0"
  val scalatestplus                 = "1.2.0"
  val silhouette                    = "2.0.2"
}

private object AppDependencies {
  import play.PlayImport._
  import play.core.PlayVersion
  import Versions._

  private val playHealthVersion = "1.1.0"
  private val playJsonLoggerVersion = "2.1.1"
  private val frontendBootstrapVersion = "6.6.0"
  private val playUiVersion = "4.16.0"
  private val playPartialsVersion = "4.4.0"
  private val playAuthorisedFrontendVersion = "5.4.0"
  private val playConfigVersion = "2.1.0"
  private val playWhitelist = "1.1.0"
  
  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap"   % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "frontend-bootstrap" % frontendBootstrapVersion,
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-authorised-frontend" % playAuthorisedFrontendVersion,
    "uk.gov.hmrc" %% "play-config" % playConfigVersion,
    "uk.gov.hmrc" %% "play-json-logger" % playJsonLoggerVersion,
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
        "org.scalatest"             %% "scalatest"                % scalatest     % "test",
        "org.scalatestplus"         %% "play"                     % scalatestplus % "test",
        "org.pegdown"               %  "pegdown"                  % pegdown       % "test",
        "org.jsoup"                 %  "jsoup"                    % jsoup         % "test",
        "com.github.tomakehurst"    %  "wiremock"                 % wiremock      % "test",
        "uk.gov.hmrc"               %% "hmrctest"                 % hmrctest      % "test"
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}


