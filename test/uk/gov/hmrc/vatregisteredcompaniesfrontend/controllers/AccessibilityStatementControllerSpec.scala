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

import org.scalatest._
import play.api.Configuration
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import play.api.i18n.{Lang, MessagesApi, Messages}
import play.api.mvc.Result
import play.api.Environment
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, status}

import scala.concurrent.Future

class AccessibilityStatementControllerSpec extends BaseSpec {

  val env: Environment = Environment.simple()
  val configuration: Configuration = Configuration.load(env)
  val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

  private implicit val messages: Messages = messagesApi.preferred(Seq.empty[Lang])
  private implicit lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  lazy val accessibilityStatementController: AccessibilityStatementController =
    app.injector.instanceOf[AccessibilityStatementController]


  "showAccessibilityStatement" should {

    "return 200 OK" in {
      val result: Future[Result] =
        accessibilityStatementController.showAccessibilityStatement.apply(
          FakeRequest(
            routes.AccessibilityStatementController.showAccessibilityStatement()
          )
        )

      status(result) should be(OK)
      contentAsString(result) should include(messagesApi("accessibility.statement.h1"))
      contentAsString(result) should include(messagesApi("accessibility.statement.intro.p1"))
      contentAsString(result) should include(messagesApi("accessibility.statement.using.heading"))
      contentAsString(result) should include(messagesApi("accessibility.statement.accessible.heading"))
    }

  }
}
