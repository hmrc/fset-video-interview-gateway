import sbt._

private object AppDependencies {
  import play.sbt.PlayImport._

  val compile = Seq(
    ws,
    "uk.gov.hmrc"                   %% "bootstrap-backend-play-28"    % Versions.bootstrapVersion,
    "uk.gov.hmrc"                   %% "bootstrap-frontend-play-28"   % Versions.bootstrapVersion,
    "com.typesafe.play"             %% "play-json-joda"               % "2.6.10",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"         % "2.12.2"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      lazy val test = Seq(
        "org.mockito"               %  "mockito-core"             % "3.9.0"               % sbt.Test,
        "org.scalatestplus.play"    %% "scalatestplus-play"       % "5.1.0"               % sbt.Test,
        // Gives you access to MockitoSugar as it is no longer available in scalatestplus-play
        "org.scalatestplus"         %% "mockito-3-4"              % "3.2.8.0"             % sbt.Test,
        "com.vladsch.flexmark"      %  "flexmark-all"             % "0.36.8"              % sbt.Test,
        "uk.gov.hmrc"               %% "bootstrap-test-play-28"   % Versions.bootstrapVersion  % sbt.Test,
        "com.github.tomakehurst"    %  "wiremock-jre8"            % "2.27.2"              % sbt.Test
      )
    }.test
  }

  object Versions {
    val bootstrapVersion = "5.24.0"
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
