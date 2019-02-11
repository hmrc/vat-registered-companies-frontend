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


import com.sun.istack.internal.NotNull
import javax.inject.Inject
import org.scalatest.{Matchers, MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.connectors.VatRegisteredCompaniesConnector
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.LookupResponse

class VatRegCoLookupControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite  {


  val fakeRequest = FakeRequest("GET", "/")

  val env = Environment.simple()
  val configuration = Configuration.load(env)


  val messageApi = new DefaultMessagesApi(env, configuration, new DefaultLangs(configuration))
  val appConfig = new AppConfig(configuration, env)


  val vfhyjfgyjg = new VatRegisteredCompaniesConnector(
    null,
    env,
    configuration)




  val controller = new VatRegCoLookupController(messageApi,vfhyjfgyjg, appConfig)

  "VatRegCoLookup Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller.start(fakeRequest)

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
    }


    "return OK and lookup form for a GET" in {
      val result = controller.lookupForm(fakeRequest)

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")

      contentAsString(result) should  include(messageApi("vatcheck.lookup.target.hint"))
      contentAsString(result) should  include(messageApi("vatcheck.lookup.withConsultationNumber.label"))
      contentAsString(result) should  include(messageApi("vatcheck.lookup.requester.label"))
      contentAsString(result) should  include(messageApi("vatcheck.lookup.requester.hint"))


    }
  }


}
