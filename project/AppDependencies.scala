import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val playVersion = "play-30"
  val bootstrapVersion = "8.2.0"
  val hmrcMongoVersion = "1.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend-$playVersion"            % bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc-$playVersion"            % "8.5.0",
    "uk.gov.hmrc"             %% s"play-conditional-form-mapping-$playVersion" % "2.0.0",
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-$playVersion"                    % hmrcMongoVersion,
    "org.typelevel"           %% "cats-core"                                   % "2.9.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"               %% s"bootstrap-test-$playVersion"   % bootstrapVersion,
    "org.playframework"         %% "play-test"                      % current,
    "org.mockito"               %  "mockito-core"                   % "5.10.0",
    "org.scalatest"             %% "scalatest"                      % "3.2.17",
    "org.scalatestplus.play"    %% "scalatestplus-play"             % "7.0.1",
    "org.scalatestplus"         %% "mockito-3-4"                    % "3.2.10.0",
    "org.jsoup"                 %  "jsoup"                          % "1.17.2",
    "org.pegdown"               %  "pegdown"                        % "1.6.0",
    "com.vladsch.flexmark"      %  "flexmark-all"                   % "0.36.8"
  ).map(_ % "test, it")

}
