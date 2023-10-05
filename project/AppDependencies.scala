import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.12.0"
  val hmrcMongoVersion = "0.74.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28"    % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"            % "5.2.0-play-28",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping" % "1.12.0-play-28",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"            % hmrcMongoVersion,
    "org.typelevel"           %% "cats-core"                     % "2.9.0"
  )

  val test = Seq(
    "uk.gov.hmrc"               %% "bootstrap-test-play-28"   % bootstrapVersion  % "test",
    "org.scalatest"             %% "scalatest"                % "3.2.9"           % "test",
    "org.jsoup"                 %  "jsoup"                    % "1.14.1"          % "test",
    "com.typesafe.play"         %% "play-test"                % current           % "test",
    "org.pegdown"               %  "pegdown"                  % "1.6.0"           % "test, it",
    "org.scalatestplus.play"    %% "scalatestplus-play"       % "5.1.0"           % "test, it",
    "org.scalatestplus"         %% "mockito-3-4"              % "3.2.9.0"         % "test, it",
    "com.vladsch.flexmark"      %  "flexmark-all"             % "0.36.8"         % "test, it",
    "org.mockito"               %  "mockito-core"             % "3.11.2"          % "test, it")

}
