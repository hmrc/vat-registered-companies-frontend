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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{ControllerComponents, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.{Application, Configuration, Environment}
import uk.gov.hmrc.hmrcfrontend.config.AccessibilityStatementConfig
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import views.html.ErrorTemplate
import views.html.vatregisteredcompaniesfrontend.{ConfirmationPage, InvalidVatNumberPage, LookupPage}

import scala.concurrent.ExecutionContext

trait BaseSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with TestWiring {

  val env: Environment = Environment.simple()
  val configuration: Configuration = Configuration.load(env)
  val fakeRequest = FakeRequest("GET", "/")
  val fakeRequestWithIp = FakeRequest().withHeaders("True-Client-IP" -> "10.0.0.1")
  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
  val cc: ControllerComponents = play.api.test.Helpers.stubControllerComponents()
  val mcc: MessagesControllerComponents = uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents()
  val messagesApi: MessagesApi = mcc.messagesApi
  implicit val lang: Lang = Lang.defaultLang
  val sc: ServicesConfig = new ServicesConfig(configuration)
  implicit val appConfig: AppConfig = new AppConfig(configuration, env, sc)
  val accessibilityStatementConfig = new AccessibilityStatementConfig(configuration)

  val lookupPage = app.injector.instanceOf[LookupPage]
  val confirmationPage = app.injector.instanceOf[ConfirmationPage]
  val invalidVatNumberPage = app.injector.instanceOf[InvalidVatNumberPage]

  val errorTemplate = app.injector.instanceOf[ErrorTemplate]

  implicit val execute: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()
}
