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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers

import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{status, _}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.utils.BaseSpec

import scala.concurrent.Future

class ExitSurveyControllerSpec extends BaseSpec {

  private val controller = new ExitSurveyController(mcc)

  "ExitSurveyController" should {
    "return 303 SEE_OTHER" in {
      val result: Future[Result] = controller.exitSurvey(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

}
