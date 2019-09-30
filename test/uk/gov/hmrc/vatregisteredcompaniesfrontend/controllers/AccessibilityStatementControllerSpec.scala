/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.i18n._
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, contentType, status, _}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import utils.TestWiring

import scala.concurrent.Future

class AccessibilityStatementControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with TestWiring {

  val fakeRequest = FakeRequest("GET", "/")
  val env = Environment.simple()

  implicit val configuration = Configuration.load(env)
  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
  implicit val appConfig = new AppConfig(configuration, env)

  val messagesApi = new DefaultMessagesApi(env, configuration, new DefaultLangs(configuration))
  val controller = new AccessibilityStatementController(messagesApi, env)

  "showAccessibilityStatement" should {

    "return 200 OK" in {
      val result: Future[Result] = controller.showAccessibilityStatement(fakeRequest)

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")

      contentAsString(result) should include(messagesApi("accessibility.statement.h1"))
      contentAsString(result) should include(messagesApi("accessibility.statement.intro.p1"))
      contentAsString(result) should include(messagesApi("accessibility.statement.using.heading"))
      contentAsString(result) should include(messagesApi("accessibility.statement.accessible.heading"))
    }

  }
}