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


import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import org.mockito.ArgumentMatchers.{any, eq => matching}
import org.mockito.Mockito.when
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.{ConsultationNumber, Lookup, _}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.services.SessionCacheService
import utils.TestWiring

import scala.concurrent.Future

class VatRegCoLookupControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with TestWiring {

  val mockSessionCache = mock[SessionCacheService]
  val testVatNumber = new VatNumber("GB987654321")
  val missingVatNumber = new VatNumber("")
  val invalidVatNumber = new VatNumber("InvalidVAT-HT6754")
  val requesterVatNo = Some(new VatNumber("GB999999999999"))
  val boolValue = true
  val vatRegCompany =  VatRegisteredCompany(
    new CompanyName("XYZ Exports"),
    new VatNumber("GB987654321"),
    Address("33 HopeGreen", None, None, None, None, None, "UK")
  )
  val lookupObj = new Lookup(testVatNumber, boolValue, requesterVatNo)

  val controller = new VatRegCoLookupController(mockVatRegCoConnector, mockSessionCache, mcc)
  when(mockSessionCache.sessionUuid(fakeRequest)).thenReturn(Some("foo"))
  when(mockSessionCache.put[Lookup](any(),any(), any())(any(),any(), any())).thenReturn {
    Future.successful(true)
  }
  when(mockSessionCache.put[LookupResponse](any(),any(),any())(any(),any(), any())).thenReturn {
    Future.successful(true)
  }

  "VatRegCoLookup Controller" must {

    "return OK and lookup form for a GET" in {

      val result = controller.lookupForm(fakeRequest)

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")

      contentAsString(result) should  include(messagesApi("vatcheck.lookup.target.hint"))
      contentAsString(result) should  include(messagesApi("vatcheck.lookup.withConsultationNumber.label"))
      contentAsString(result) should  include(messagesApi("vatcheck.lookup.requester.label"))
      contentAsString(result) should  include(messagesApi("vatcheck.lookup.requester.hint"))

    }

    "return OK and get lookupResponse object for a POST" in {

      val request = FakeRequest("POST", "/enter-vat-details").withFormUrlEncodedBody("target" -> testVatNumber, "withConsultationNumber" -> boolValue.toString, "requester" -> requesterVatNo.getOrElse(""))
      val lookupResponseObj = new LookupResponse(Some(vatRegCompany), requesterVatNo, Some(new ConsultationNumber("Consul9999")), ZonedDateTime.of(LocalDateTime.now,ZoneId.of("Europe/London")))



      when(mockVatRegCoConnector.lookup(any())(any(), any())).thenReturn {
       Future.successful(Some(lookupResponseObj))
      }

      val result = controller.submit()(request)

      status(result) shouldBe Status.SEE_OTHER

    }

    "return OK and get invalid Reg No for a non-existing VAT No for a POST" in {

      val request = FakeRequest("POST", "/enter-vat-details")
        .withFormUrlEncodedBody(
          "target" -> testVatNumber,
          "withConsultationNumber" -> boolValue.toString,
          "requester" -> requesterVatNo.getOrElse(""))
      val lookupResponseObj = new LookupResponse(None, requesterVatNo, Some(new ConsultationNumber("Consul9999")), ZonedDateTime.of(LocalDateTime.now,ZoneId.of("Europe/London")))


      when(mockVatRegCoConnector.lookup(matching(lookupObj))(any(), any())).thenReturn {
        Future.successful(Some(lookupResponseObj))
      }

      val result = controller.submit()(request)

      status(result) shouldBe Status.SEE_OTHER

      redirectLocation(result).get shouldBe "/check-vat-number/unknown/requester/known"

    }

    "return BadRequest Exception for an empty VAT RegNo input" in {

      val request = FakeRequest("POST", "/enter-vat-details").withFormUrlEncodedBody("target" -> missingVatNumber, "withConsultationNumber" -> boolValue.toString, "requester" -> requesterVatNo.getOrElse(""))
      val result = controller.submit()(request)

      status(result) shouldBe Status.BAD_REQUEST

    }

    "return BadRequest Exception for an invalid VAT RegNo input" in {

      val request = FakeRequest("POST", "/enter-vat-details").withFormUrlEncodedBody("target" -> invalidVatNumber, "withConsultationNumber" -> boolValue.toString, "requester" -> requesterVatNo.getOrElse(""))
      val result = controller.submit()(request)

      status(result) shouldBe Status.BAD_REQUEST

    }

  }

}
