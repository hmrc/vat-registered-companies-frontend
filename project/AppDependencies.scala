import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "bootstrap-play-26"             % "4.0.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"            % "0.79.0-play-26",
    "uk.gov.hmrc"             %% "play-frontend-govuk"           % "0.79.0-play-26",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping" % "1.9.0-play-26",
    "uk.gov.hmrc"             %% "play-language"                 % "5.1.0-play-26",
    "uk.gov.hmrc"             %% "simple-reactivemongo"          % "8.0.0-play-26",
    "uk.gov.hmrc"             %% "mongo-caching"                 % "7.0.0-play-26",
    "org.typelevel"           %% "cats-core"                     % "2.4.2",
    compilerPlugin("com.github.ghik" % "silencer-plugin" % "1.7.5" cross CrossVersion.full),
    "com.github.ghik" % "silencer-lib" % "1.7.5" % Provided cross CrossVersion.full
  )

  val test = Seq(
    "org.scalatest"             %% "scalatest"                % "3.0.9"           % "test",
    "org.jsoup"                 %  "jsoup"                    % "1.10.2"          % "test",
    "com.typesafe.play"         %% "play-test"                % current           % "test",
    "org.pegdown"               %  "pegdown"                  % "1.6.0"           % "test, it",
    "uk.gov.hmrc"               %% "service-integration-test" % "1.1.0-play-26"   % "test, it",
    "org.scalatestplus.play"    %% "scalatestplus-play"       % "3.1.3"           % "test, it",
    "org.mockito"               %  "mockito-core"             % "2.24.0"          % "test, it",
    "uk.gov.hmrc"               %% "bootstrap-play-26"        % "4.0.0"           % Test classifier "tests"
  )

}
