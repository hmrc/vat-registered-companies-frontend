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


import java.time.{LocalDateTime, ZoneId}

import org.mockito.ArgumentMatchers.{any, eq => matching}
import org.mockito.Mockito.when
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.{ConsultationNumber, Lookup, _}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.services.SessionCacheService
import utils.TestWiring

import scala.concurrent.Future

class VatRegCoLookupControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with TestWiring {


  val fakeRequest = FakeRequest("GET", "/")
  val env = Environment.simple()
  val configuration = Configuration.load(env)

  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
  val messagesApi = new DefaultMessagesApi(env, configuration, new DefaultLangs(configuration))
  implicit val appConfig = new AppConfig(configuration, env)

  val mockSessionCache = mock[SessionCacheService]
  val controller = new VatRegCoLookupController(messagesApi, mockAuthConnector, mockSessionCache, appConfig)

  "VatRegCoLookup Controller" must {

    "return OK and lookup form for a GET" in {

      when(mockSessionCache.sessionUuid(fakeRequest)).thenReturn(Some("foo"))

      val result = controller.lookupForm(fakeRequest)

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")

      contentAsString(result) should  include(messagesApi("vatcheck.lookup.target.hint"))
      contentAsString(result) should  include(messagesApi("vatcheck.lookup.withConsultationNumber.label"))
      contentAsString(result) should  include(messagesApi("vatcheck.lookup.requester.label"))
      contentAsString(result) should  include(messagesApi("vatcheck.lookup.requester.hint"))


    }

    "return OK and get lookupResponse object for a POST" in {
      when(mockSessionCache.sessionUuid(fakeRequest)).thenReturn(Some("foo"))
      val testVatNumber = new VatNumber("GB987654321")
      val requesterVatNo = Some(new VatNumber("GB999999999999"))
      val boolValue = true
      val vatRegCompany =  VatRegisteredCompany(
                                       new CompanyName("XYZ Exports"),
                                        new VatNumber("GB987654321"),
                                       Address("33 HopeGreen", None, None, None, None, None, "UK")
                                     )

      val request = FakeRequest("POST", "/enter-vat-details").withFormUrlEncodedBody("target" -> testVatNumber, "withConsultationNumber" -> boolValue.toString, "requester" -> requesterVatNo.getOrElse(""))
      val lookupResponseObj = new LookupResponse(Some(vatRegCompany), requesterVatNo, Some(new ConsultationNumber("Consul9999")), LocalDateTime.now(ZoneId.of("Europe/London") ))


      when(mockAuthConnector.lookup(any())(any(), any())).thenReturn {
       Future.successful(Some(lookupResponseObj))
      }

      val result = controller.submit()(request)

      status(result) shouldBe Status.SEE_OTHER

    }

    "return OK and get invalid Reg No for a non-existing VAT No for a POST" in {
      when(mockSessionCache.sessionUuid(fakeRequest)).thenReturn(Some("foo"))
      val testVatNumber = new VatNumber("GB987654321")
      val requesterVatNo = Some(new VatNumber("GB999999999999"))
      val boolValue = true
      val vatRegCompany =  VatRegisteredCompany(
        new CompanyName("XYZ Exports"),
        new VatNumber("GB987654321"),
        Address("33 HopeGreen", None, None, None, None, None, "UK")
      )

      val lookupObj = new Lookup(testVatNumber, boolValue, requesterVatNo)
      val request = FakeRequest("POST", "/enter-vat-details")
        .withFormUrlEncodedBody(
          "target" -> testVatNumber,
          "withConsultationNumber" -> boolValue.toString,
          "requester" -> requesterVatNo.getOrElse(""))
      val lookupResponseObj = new LookupResponse(None, requesterVatNo, Some(new ConsultationNumber("Consul9999")), LocalDateTime.now(ZoneId.of("Europe/London") ))


      when(mockAuthConnector.lookup(matching(lookupObj))(any(), any())).thenReturn {
        Future.successful(Some(lookupResponseObj))
      }

      when(mockSessionCache.get[LookupResponse](any(), any())(any(), any(), any())).thenReturn {
        Future.successful(Some(lookupResponseObj))
      }

      val result = controller.submit()(request)

      status(result) shouldBe Status.SEE_OTHER

      redirectLocation(result).get shouldBe "/check-vat-number/unknown/requester/known"

    }

    "return BadRequest Exception for an empty VAT RegNo input" in {

      val testVatNumber = new VatNumber("")
      val requesterVatNo = Some(new VatNumber("GB999999999999"))
      val boolValue = true
      val request = FakeRequest("POST", "/enter-vat-details").withFormUrlEncodedBody("target" -> testVatNumber, "withConsultationNumber" -> boolValue.toString, "requester" -> requesterVatNo.getOrElse(""))
      val result = controller.submit()(request)

      status(result) shouldBe Status.BAD_REQUEST

    }

    "return BadRequest Exception for an invalid VAT RegNo input" in {

      val testVatNumber = new VatNumber("InvalidVAT-HT6754")
      val requesterVatNo = Some(new VatNumber("GB999999999999"))
      val boolValue = true
      val request = FakeRequest("POST", "/enter-vat-details").withFormUrlEncodedBody("target" -> testVatNumber, "withConsultationNumber" -> boolValue.toString, "requester" -> requesterVatNo.getOrElse(""))
      val result = controller.submit()(request)

      status(result) shouldBe Status.BAD_REQUEST

    }

  }


}
