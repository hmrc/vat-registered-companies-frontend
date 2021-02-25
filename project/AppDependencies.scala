import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "bootstrap-play-26"             % "2.0.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"            % "0.27.0-play-26",
    "uk.gov.hmrc"             %% "play-frontend-govuk"           % "0.53.0-play-26",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping" % "1.3.0-play-26",
    "uk.gov.hmrc"             %% "play-language"                 % "4.4.0-play-26",
    "uk.gov.hmrc"             %% "simple-reactivemongo"          % "7.30.0-play-26",
    "uk.gov.hmrc"             %% "mongo-caching"                 % "6.15.0-play-26",
    "org.typelevel"           %% "cats-core"                     % "1.1.0"
  )

  val test = Seq(
    "org.scalatest"             %% "scalatest"                % "3.0.4"                 % "test",
    "org.jsoup"                 %  "jsoup"                    % "1.10.2"                % "test",
    "com.typesafe.play"         %% "play-test"                % current                 % "test",
    "org.pegdown"               %  "pegdown"                  % "1.6.0"                 % "test, it",
    "uk.gov.hmrc"               %% "service-integration-test" % "0.2.0"                 % "test, it",
    "org.scalatestplus.play"    %% "scalatestplus-play"       % "3.1.2"                 % "test, it",
    "org.mockito"               %  "mockito-core"             % "2.24.0"                % "test, it",
    "uk.gov.hmrc"               %% "bootstrap-play-26"        % "2.0.0"                 % Test classifier "tests"
  )

}
