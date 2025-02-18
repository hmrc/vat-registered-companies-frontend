val appName = "vat-registered-companies-frontend"

PlayKeys.playDefaultPort := 8730

scalaVersion := "3.3.4"

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := "<empty>;views.*;prod.*;uk.gov.hmrc.vatregisteredcompaniesfrontend.connectors.*;.*models.*;.*test.*",
    ScoverageKeys.coverageExcludedFiles := "<empty>;.*BuildInfo.*;.*Routes.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 83,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}


lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._"
    ),
    scoverageSettings
  )
  .configs(IntegrationTest)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(scalacOptions ++= Seq(
    "-Wconf:msg=unused-imports:silent",
    "-Wconf:src=routes/.*:s",
    "-Wconf:msg=Flag.*repeatedly:s",
    "-Wconf:src=views/.*html.*:s"))
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
