import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "govuk-template"                   % "5.26.0-play-25",
    "uk.gov.hmrc"             %% "play-ui"                          % "7.31.0-play-25",
    "uk.gov.hmrc"             %% "bootstrap-play-25"                % "4.8.0",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping"    % "0.2.0",
    "uk.gov.hmrc"             %% "play-language"                    % "3.0.0",
    "uk.gov.hmrc"             %% "play-reactivemongo"               % "6.4.0",
    "uk.gov.hmrc"             %% "simple-reactivemongo"             % "7.12.0-play-25",
    "uk.gov.hmrc"             %% "mongo-caching"                    % "6.1.0-play-25",
    "org.typelevel"           %% "cats-core"                        % "1.1.0"
  )

  val test = Seq(
    "org.scalatest"             %% "scalatest"                % "3.0.4"                 % "test",
    "org.jsoup"                 %  "jsoup"                    % "1.10.2"                % "test",
    "com.typesafe.play"         %% "play-test"                % current                 % "test",
    "org.pegdown"               %  "pegdown"                  % "1.6.0"                 % "test, it",
    "uk.gov.hmrc"               %% "service-integration-test" % "0.2.0"                 % "test, it",
    "org.scalatestplus.play"    %% "scalatestplus-play"       % "2.0.0"                 % "test, it",
    "org.mockito"               %  "mockito-core"             % "2.13.0"                % "test, it"
  )

}
