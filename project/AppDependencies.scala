import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val playVersion = "play-30"
  val bootstrapVersion = "9.6.0"
  val hmrcMongoVersion = "2.4.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend-$playVersion"            % bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc-$playVersion"            % "11.9.0",
    "uk.gov.hmrc"             %% s"play-conditional-form-mapping-$playVersion" % "3.2.0",
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-$playVersion"                    % hmrcMongoVersion,
    "org.typelevel"           %% "cats-core"                                   % "2.12.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"               %% s"bootstrap-test-$playVersion" % bootstrapVersion,
    "org.playframework"         %% "play-test"                    % current,
    "org.mockito"               %  "mockito-core"                 % "5.13.0",
    "org.jsoup"                 %  "jsoup"                        % "1.18.1",
    "org.pegdown"               %  "pegdown"                      % "1.6.0"
  ).map(_ % "test, it")

}
