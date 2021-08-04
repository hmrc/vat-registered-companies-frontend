import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28"    % "5.7.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"            % "0.83.0-play-28",
    "uk.gov.hmrc"             %% "play-frontend-govuk"           % "0.80.0-play-28",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping" % "1.9.0-play-28",
    "uk.gov.hmrc"             %% "play-language"                 % "5.1.0-play-28",
    "uk.gov.hmrc"             %% "simple-reactivemongo"          % "8.0.0-play-28",
    "uk.gov.hmrc"             %% "mongo-caching"                 % "7.0.0-play-28",
    "org.typelevel"           %% "cats-core"                     % "2.4.2",
    compilerPlugin("com.github.ghik" % "silencer-plugin" % "1.7.5" cross CrossVersion.full),
    "com.github.ghik" % "silencer-lib" % "1.7.5" % Provided cross CrossVersion.full
  )

  val test = Seq(
    "uk.gov.hmrc"               %% "bootstrap-test-play-28"   % "5.7.0"           % "test",
    "org.scalatest"             %% "scalatest"                % "3.2.9"           % "test",
    "org.jsoup"                 %  "jsoup"                    % "1.14.1"          % "test",
    "com.typesafe.play"         %% "play-test"                % current           % "test",
    "org.pegdown"               %  "pegdown"                  % "1.6.0"           % "test, it",
    "uk.gov.hmrc"               %% "service-integration-test" % "1.1.0-play-28"   % "test, it",
    "org.scalatestplus.play"    %% "scalatestplus-play"       % "5.1.0"           % "test, it",
    "org.scalatestplus"         %% "mockito-3-4"              % "3.2.9.0"         % "test, it",
    "com.vladsch.flexmark"      %  "flexmark-all"             % "0.36.8"         % "test, it",
    "org.mockito"               %  "mockito-core"             % "3.11.2"          % "test, it")

}
