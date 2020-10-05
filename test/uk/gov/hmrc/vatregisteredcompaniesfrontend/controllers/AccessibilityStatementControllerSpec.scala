/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.mvc.Result
import play.api.test.Helpers.{contentAsString, contentType, status, _}
import utils.{BaseSpec, TestWiring}

import scala.concurrent.Future

class AccessibilityStatementControllerSpec extends BaseSpec {

  // TODO - not updating to use injected templates as moving to using accessibilty service (DST-330)

//  val controller = new AccessibilityStatementController(mcc, env, ???)

  "showAccessibilityStatement" should {
    "return 200 OK" in (pending)
//    {
//      val result: Future[Result] = controller.showAccessibilityStatement("foo")(fakeRequest)
//
//      status(result) shouldBe Status.OK
//      contentType(result) shouldBe Some("text/html")
//      contentAsString(result) should include(messagesApi("accessibility.statement.h1"))
//      contentAsString(result) should include(messagesApi("accessibility.statement.intro.p1"))
//      contentAsString(result) should include(messagesApi("accessibility.statement.using.heading"))
//      contentAsString(result) should include(messagesApi("accessibility.statement.accessible.heading"))
//    }

  }
}
