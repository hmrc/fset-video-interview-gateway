import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object FrontendBuild extends Build with MicroService {

  val appName = "fset-video-interview-gateway"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object Versions {
  val hmrcMicroserviceBootstrapVersion  = "8.3.0"
  //val pegdown                       = "1.5.0"
 // val jsoup                         = "1.11.3"
  val wiremock                      = "2.19.0"
  val hmrctest                      = "3.1.0"
  val scalatestplus                 = "1.5.1"
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import Versions._

  private val frontendBootstrapVersion = "10.4.0"
  private val playPartialsVersion = "6.1.0"
  private val playAuthorisedFrontendVersion = "7.0.0"
  private val playWhitelist = "2.0.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap"   % hmrcMicroserviceBootstrapVersion,
    "uk.gov.hmrc" %% "frontend-bootstrap" % frontendBootstrapVersion,
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-authorised-frontend" % playAuthorisedFrontendVersion,
    "uk.gov.hmrc" %% "play-whitelist-filter" % playWhitelist
  )

  trait TestDependencies {
    lazy val scope: String = "test"
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      lazy val test = Seq(
        "org.mockito" % "mockito-all" % "1.10.19",
        "org.scalatestplus.play"    %% "scalatestplus-play"       % scalatestplus % "test",
        //"org.pegdown"               %  "pegdown"                  % pegdown       % "test",
        //"org.jsoup"                 %  "jsoup"                    % jsoup         % "test",
        "com.github.tomakehurst"    %  "wiremock"                 % wiremock      % "test",
        "uk.gov.hmrc"               %% "hmrctest"                 % hmrctest      % "test"
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
