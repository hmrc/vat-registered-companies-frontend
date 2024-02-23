import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "8.2.0"
  val hmrcMongoVersion = "1.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"            % "8.5.0",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping-play-30" % "2.0.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                    % hmrcMongoVersion,
    "org.typelevel"           %% "cats-core"                             % "2.9.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"               %% "bootstrap-test-play-30"   % bootstrapVersion  % "test",
    "org.scalatest"             %% "scalatest"                % "3.2.9"           % "test",
    "org.jsoup"                 %  "jsoup"                    % "1.14.1"          % "test",
    "org.playframework"         %% "play-test"                % current           % "test",
    "org.pegdown"               %  "pegdown"                  % "1.6.0"           % "test, it",
    "org.scalatestplus.play"    %% "scalatestplus-play"       % "5.1.0"           % "test, it",
    "org.scalatestplus"         %% "mockito-3-4"              % "3.2.9.0"         % "test, it",
    "com.vladsch.flexmark"      %  "flexmark-all"             % "0.36.8"         % "test, it",
    "org.mockito"               %  "mockito-core"             % "3.11.2"          % "test, it")

}
