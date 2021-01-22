import sbt._

private object AppDependencies {
  import play.sbt.PlayImport._

  val compile = Seq(
    ws,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-27"    % Versions.playVersion,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-27"   % Versions.playVersion,
    "com.typesafe.play" %% "play-json-joda"               % "2.6.10"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      lazy val test = Seq(
        "org.pegdown"               %  "pegdown"                  % "1.6.0"               % sbt.Test,
        "org.mockito"               %  "mockito-all"              % "1.10.19"             % sbt.Test,
        "org.scalatestplus.play"    %% "scalatestplus-play"       % "4.0.3"               % sbt.Test,
        "uk.gov.hmrc"               %% "bootstrap-test-play-27"   % Versions.playVersion  % sbt.Test,
        "com.github.tomakehurst"    %  "wiremock-jre8"            % "2.27.2"              % sbt.Test
      )
    }.test
  }

  object Versions {
    val playVersion = "2.25.0"
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
