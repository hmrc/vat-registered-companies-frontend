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
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import utils.TestWiring

import scala.concurrent.Future


class ExitSurveyControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with TestWiring {
  private val env: Environment = Environment.simple()
  private val configuration: Configuration = Configuration.load(env)

  implicit val appConfig: AppConfig = new AppConfig(configuration, env)
  private val controller = new ExitSurveyController()
  private val fakeRequest = FakeRequest("GET", "/")

  "ExitSurveyController" should {
    "return 303 SEE_OTHER" in {
      val result: Future[Result] = controller.exitSurvey(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

}