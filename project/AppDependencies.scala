import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val playVersion = "play-30"
  val bootstrapVersion = "9.0.0"
  val hmrcMongoVersion = "2.1.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend-$playVersion"            % bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc-$playVersion"            % "10.3.0",
    "uk.gov.hmrc"             %% s"play-conditional-form-mapping-$playVersion" % "2.0.0",
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-$playVersion"                    % hmrcMongoVersion,
    "org.typelevel"           %% "cats-core"                                   % "2.12.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"               %% s"bootstrap-test-$playVersion" % bootstrapVersion,
    "org.playframework"         %% "play-test"                    % current,
    "org.mockito"               %  "mockito-core"                 % "5.12.0",
    "org.jsoup"                 %  "jsoup"                        % "1.17.2",
    "org.pegdown"               %  "pegdown"                      % "1.6.0"
  ).map(_ % "test, it")

}
