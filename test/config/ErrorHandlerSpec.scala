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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.config


import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.test.FakeRequest
import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.play.bootstrap.config.{ServicesConfig, RunMode}



class ErrorHandlerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {


  val fakeRequest = FakeRequest("GET", "/")

  val env = Environment.simple()
  val configuration = Configuration.load(env)


  val messagesApi = new DefaultMessagesApi()
  val servicesConfig = new ServicesConfig(configuration, new RunMode(configuration, Mode.Test))
  val appConfig = new AppConfig(configuration, env, servicesConfig)

  val errHandler = new ErrorHandler( messagesApi = messagesApi, appConfig= appConfig)

  "ErrorHandler should" must {

    "return an error page" in {
      val result = errHandler.standardErrorTemplate("samplePageTitle", "sampleHeading", "sampleMessage")(fakeRequest)

      result.body should include("samplePageTitle")


    }


  }


}
