/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.services


import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AnyContent, Request, Session}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatregisteredcompaniesfrontend.VatRegisteredCompaniesService
import uk.gov.hmrc.vatregisteredcompaniesfrontend.connectors.VatRegisteredCompaniesConnector
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.{Address, Lookup, LookupResponse, VatRegisteredCompany}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.utils.BaseSpec

import java.time.{ZoneId, ZonedDateTime}
import scala.concurrent.Future
import scala.reflect.ClassTag

class VatRegisteredCompaniesServiceSpec extends BaseSpec  with MockitoSugar with ScalaFutures {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val lookupResponseFormat: OFormat[LookupResponse] = Json.format[LookupResponse]
  implicit val testLookupResponseFormat: OFormat[LookupResponse] = Json.format[LookupResponse]


  "VatRegisteredCompaniesService" should {
    "retrieve cached lookup response if present" in {
      val mockSessionCacheService = mock[SessionCacheService]
      val mockConnector = mock[VatRegisteredCompaniesConnector]
      val service = new VatRegisteredCompaniesService(mockSessionCacheService, mockConnector)

      val lookup = Lookup("123456789")
      val testAddress = Address(
        line1 = "123 Example Street",
        line2 = Some("Example Town"),
        line3 = Some("Example County"),
        line4 = None,
        line5 = None,
        postcode = Some("EX1 1EX"),
        countryCode = "GB"
      )
      val target = Some(VatRegisteredCompany("CompanyName", "123456789",testAddress))
      val requester = Some("987654321")
      val consultationNumber = Some("CONSULT123")
      val processingDate = ZonedDateTime.now.withZoneSameInstant(ZoneId.of("Europe/London"))

      val response = LookupResponse(target, requester, consultationNumber, processingDate)

      val cacheId = Json.toJson(lookup).toString()

      when(mockSessionCacheService.toString).thenReturn("MockedSessionCacheService")
      when(mockSessionCacheService.get[LookupResponse](
        eqTo(cacheId),
        eqTo(service.responseCacheId)
      )(any[ClassTag[LookupResponse]], any[HeaderCarrier], any(), any[OFormat[LookupResponse]]))
        .thenReturn(Future.successful(Some(response)))


      val result = service.lookupVatComp(lookup).futureValue

      result shouldBe response

    }

    "call the connector and cache response if no cached data is present" in {
      val mockSessionCacheService = mock[SessionCacheService]
      val mockConnector = mock[VatRegisteredCompaniesConnector]
      val service = new VatRegisteredCompaniesService(mockSessionCacheService, mockConnector)

      val lookup = Lookup("123456789")
      val testAddress = Address(
        line1 = "123 Example Street",
        line2 = Some("Example Town"),
        line3 = Some("Example County"),
        line4 = None,
        line5 = None,
        postcode = Some("EX1 1EX"),
        countryCode = "GB"
      )
      val target = Some(VatRegisteredCompany("CompanyName", "123456789",testAddress))
      val requester = Some("987654321")
      val consultationNumber = Some("CONSULT123")
      val processingDate = ZonedDateTime.now.withZoneSameInstant(ZoneId.of("Europe/London"))

      val response = LookupResponse(target, requester, consultationNumber, processingDate)
      val cacheId = Json.toJson(lookup).toString()

      when(mockSessionCacheService.get[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId))(any[ClassTag[LookupResponse]],any(), any(), any()))
        .thenReturn(Future.successful(None))

      when(mockConnector.lookup(eqTo(lookup))(any(), any()))
        .thenReturn(Future.successful(response))

      when(mockSessionCacheService.put[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId), eqTo(response))(any(), any(), any()))
        .thenReturn(Future.successful(true))

      val result = service.lookupVatComp(lookup).futureValue

      result shouldBe response

    }

//    "return None if cache ID is missing in session" in {
//      val mockSessionCacheService = mock[SessionCacheService]
//      val mockConnector = mock[VatRegisteredCompaniesConnector]
//      val service = new VatRegisteredCompaniesService(mockSessionCacheService, mockConnector)
//
//      val lookup = Lookup("123456789")
//      val cacheId = Json.toJson(lookup).toString()
//
//      val mockRequest = mock[Request[AnyContent]]
////      when(mockRequest.session).thenReturn(Session())
//
////      when(mockRequest.session.get("cacheId")).thenReturn(None)
//
//      when(mockSessionCacheService.get[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId))(any[ClassTag[LookupResponse]],any(), any(), any()))
//        .thenReturn(Future.successful(None))
//
//      val result = service.getLookupFromCache(eqTo(mockRequest), any()).value.futureValue
//
//      result shouldBe None
//    }

//    "retrieve lookup from cache ID when present in session" in {
//      val mockSessionCacheService = mock[SessionCacheService]
//      val mockConnector = mock[VatRegisteredCompaniesConnector]
//      val service = new VatRegisteredCompaniesService(mockSessionCacheService, mockConnector)
//
//      val lookup = Lookup("123456789")
//      val cacheId = Json.toJson(lookup).toString()
//
//      val mockRequest = mock[Request[AnyContent]]
//      when(mockRequest.session).thenReturn(Session(Map("cacheId" -> cacheId)))
//
//      val result = service.getLookupFromCache(mockRequest, any()).value.futureValue
//
//      result shouldBe Some(lookup)
//    }

//    "return None if cache ID in session is invalid" in {
//      val mockSessionCacheService = mock[SessionCacheService]
//      val mockConnector = mock[VatRegisteredCompaniesConnector]
//      val service = new VatRegisteredCompaniesService(mockSessionCacheService, mockConnector)
//
//      val invalidCacheId = "invalid-json"
//
//      val mockRequest = mock[Request[AnyContent]]
//      when(mockRequest.session).thenReturn(Session(Map("cacheId" -> invalidCacheId)))
//
//      val result = service.getLookupFromCache(mockRequest, any()).value.futureValue
//
//      result shouldBe None
//    }

    "retrieve lookup response from cache if cache ID is valid" in {
      val mockSessionCacheService = mock[SessionCacheService]
      val mockConnector = mock[VatRegisteredCompaniesConnector]
      val service = new VatRegisteredCompaniesService(mockSessionCacheService, mockConnector)

      val testAddress = Address(
        line1 = "123 Example Street",
        line2 = Some("Example Town"),
        line3 = Some("Example County"),
        line4 = None,
        line5 = None,
        postcode = Some("EX1 1EX"),
        countryCode = "GB"
      )
      val target = Some(VatRegisteredCompany("CompanyName", "123456789",testAddress))
      val requester = Some("987654321")
      val consultationNumber = Some("CONSULT123")
      val processingDate = ZonedDateTime.now.withZoneSameInstant(ZoneId.of("Europe/London"))

      val lookupResponse = LookupResponse(target, requester, consultationNumber, processingDate)

      val cacheId = "valid-cache-id"

      val mockRequest = mock[Request[AnyContent]]
      when(mockRequest.session).thenReturn(Session(Map("cacheId" -> cacheId)))

      when(mockSessionCacheService.get[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId))(any[ClassTag[LookupResponse]],any(), any(), any()))
        .thenReturn(Future.successful(Some(lookupResponse)))

      val result = service.getLookupResponseFromCache(eqTo(mockRequest), hc, any()).value.futureValue

      result shouldBe Some(lookupResponse)
    }

//    "return None when lookup response is missing in cache" in {
//      val mockSessionCacheService = mock[SessionCacheService]
//      val mockConnector = mock[VatRegisteredCompaniesConnector]
//      val service = new VatRegisteredCompaniesService(mockSessionCacheService, mockConnector)
//
//      val cacheId = "valid-cache-id"
//
//      val testAddress = Address(
//        line1 = "123 Example Street",
//        line2 = Some("Example Town"),
//        line3 = Some("Example County"),
//        line4 = None,
//        line5 = None,
//        postcode = Some("EX1 1EX"),
//        countryCode = "GB"
//      )
//
//      val mockRequest = mock[Request[AnyContent]]
//      when(mockRequest.session).thenReturn(Session(Map("cacheId" -> cacheId)))
//
//      when(mockSessionCacheService.get[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId))(any[ClassTag[LookupResponse]],any(), any(), any()))
//        .thenReturn(Future.successful(None))
//
//      val result = service.getLookupResponseFromCache(mockRequest, hc, any()).value.futureValue
//
//      result shouldBe None
//    }
  }
}
