import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val playVersion = "play-30"
  val bootstrapVersion = "9.19.0"
  val hmrcMongoVersion = "2.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend-$playVersion"            % bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc-$playVersion"            % "12.8.0",
    "uk.gov.hmrc"             %% s"play-conditional-form-mapping-$playVersion" % "3.3.0",
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-$playVersion"                    % hmrcMongoVersion,
    "org.typelevel"           %% "cats-core"                                   % "2.13.0",
    "com.digitaltangible"     %% "play-guard"                                  % "3.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"               %% s"bootstrap-test-$playVersion" % bootstrapVersion,
    "org.playframework"         %% "play-test"                    % current,
    "org.mockito"               %  "mockito-core"                 % "5.19.0",
    "org.jsoup"                 %  "jsoup"                        % "1.21.2",
    "com.vladsch.flexmark"      % "flexmark-all"                  % "0.64.8"
  ).map(_ % "test, it")

}
