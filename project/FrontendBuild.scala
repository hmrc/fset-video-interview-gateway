import play.sbt.PlayImport.ws
import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object FrontendBuild extends Build with MicroService {
  val appName = "fset-video-interview-gateway"
  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}


private object AppDependencies {
  import play.sbt.PlayImport._

  val compile = Seq(
    ws,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-26"    % "2.25.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-26"   % "2.25.0",
    "com.typesafe.play" %% "play-json-joda"               % "2.6.10"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      lazy val test = Seq(
        "org.mockito"               %  "mockito-all"              % "1.10.19",
        "org.scalatestplus.play"    %% "scalatestplus-play"       % "3.1.3"       % "test",
        "uk.gov.hmrc"               %% "hmrctest"                 % "3.0.0"       % "test",
        "uk.gov.hmrc"               %% "bootstrap-test-play-26"   % "2.24.0",
        "com.github.tomakehurst"    %  "wiremock-jre8"            % "2.27.2"      % "test"
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
