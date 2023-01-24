/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.vatregisteredcompaniesfrontend.config

import javax.inject.{Inject, Singleton}
import play.api.i18n.Lang
import play.api.mvc.Call
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers.routes

import scala.concurrent.duration.Duration

@Singleton
class AppConfig @Inject()(
  val config: Configuration,
  environment: Environment,
  servicesConfig: ServicesConfig) {

  private def loadConfig(key: String) = config.getOptional[String](key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  lazy val appName: String = config.get[String]("appName")

  private val contactHost = config.getOptional[String](s"contact-frontend.host").getOrElse("")
  private val contactFormServiceIdentifier = loadConfig("appName")

  lazy val assetsPrefix: String = loadConfig(s"assets.url") + loadConfig(s"assets.version")
  lazy val analyticsToken: String = loadConfig(s"google-analytics.token")
  lazy val analyticsHost: String = loadConfig(s"google-analytics.host")
  lazy val feedbackSurveyUrl: String = loadConfig("microservice.services.feedback-survey.url")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrlAuth = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrlNoAuth = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))

  def routeToSwitchLanguage: String => Call = (lang: String) => routes.CustomLanguageSwitchController.switchToLanguage(lang)

  lazy val languageTranslationEnabled: Boolean =
    config.getOptional[Boolean]("microservice.services.features.welsh-translation").getOrElse(true)

  val mongoSessionExpireAfter: Duration = servicesConfig.getDuration("mongodb.session.expireAfter")

}