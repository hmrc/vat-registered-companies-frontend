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
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.{Address, CompanyName, Lookup, LookupResponse, VatNumber, VatRegisteredCompany}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.utils.BaseSpec

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}
import scala.concurrent.Future
import scala.reflect.ClassTag

class VatRegisteredCompaniesServiceSpec extends BaseSpec  with MockitoSugar with ScalaFutures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val lookupResponseFormat: OFormat[LookupResponse] = Json.format[LookupResponse]
  implicit val testLookupResponseFormat: OFormat[LookupResponse] = Json.format[LookupResponse]

  val mockSessionCacheService: SessionCacheService = mock[SessionCacheService]
  val mockConnector: VatRegisteredCompaniesConnector = mock[VatRegisteredCompaniesConnector]

  val testVatNumber = new VatNumber("GB987654321")
  val missingVatNumber = new VatNumber("")
  val invalidVatNumber = new VatNumber("InvalidVAT-HT6754")
  val requesterVatNo: Some[VatNumber] = Some(new VatNumber("GB999999999999"))
  val boolValue = true
  val vatRegCompany: VatRegisteredCompany =  VatRegisteredCompany(
    new CompanyName("XYZ Exports"),
    new VatNumber("GB987654321"),
    Address("33 HopeGreen", None, None, None, None, None, "UK")
  )
  val lookupObj = new Lookup(testVatNumber, boolValue, requesterVatNo)

  val target: Some[VatRegisteredCompany] = Some(VatRegisteredCompany("CompanyName", "123456789",Address("33 HopeGreen", None, None, None, None, None, "UK")))
  val requester: Some[CompanyName] = Some("987654321")
  val consultationNumber: Some[CompanyName] = Some("CONSULT123")
  val processingDate: ZonedDateTime = ZonedDateTime.now.withZoneSameInstant(ZoneId.of("Europe/London"))

  val response: LookupResponse = LookupResponse(target, requester, consultationNumber, processingDate)

  val cacheId: String = Json.toJson(lookupObj).toString()
  val service = new VatRegisteredCompaniesService(mockSessionCacheService, mockConnector)
  val mockRequest: Request[AnyContent] = mock[Request[AnyContent]]
  val invalidCacheId = "invalid-json"
  val lookupResponse: LookupResponse = LookupResponse(target, requester, consultationNumber, processingDate)


  "VatRegisteredCompaniesService" should {
    "retrieve cached lookup response if present" in {
      when(mockSessionCacheService.toString).thenReturn("MockedSessionCacheService")
      when(mockSessionCacheService.get[LookupResponse](
        eqTo(cacheId),
        eqTo(service.responseCacheId)
      )(any[ClassTag[LookupResponse]], any[HeaderCarrier], any(), any[OFormat[LookupResponse]]))
        .thenReturn(Future.successful(Some(response)))


      val result = service.lookupVatComp(lookupObj).futureValue

      result shouldBe response

    }

    "call the connector and cache response if no cached data is present" in {
      when(mockSessionCacheService.get[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId))(any[ClassTag[LookupResponse]],any(), any(), any()))
        .thenReturn(Future.successful(None))

      when(mockConnector.lookup(eqTo(lookupObj))(any(), any()))
        .thenReturn(Future.successful(response))

      when(mockSessionCacheService.put[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId), eqTo(response))(any(), any(), any()))
        .thenReturn(Future.successful(true))

      val result = service.lookupVatComp(lookupObj).futureValue

      result shouldBe response

    }

    "return None if cache ID is missing in session" in {
      when(mockSessionCacheService.toString).thenReturn("MockedSessionCacheService")
      when(mockSessionCacheService.get[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId))(any[ClassTag[LookupResponse]],any(), any(), any()))
        .thenReturn(Future.successful(None))

      val result = service.getLookupResponseFromCache(fakeRequest, hc, execute).value.futureValue

      result shouldBe None
    }

    "retrieve lookup from cache ID when present in session" in {
      when(mockRequest.session).thenReturn(Session(Map("cacheId" -> cacheId)))

      when(mockSessionCacheService.toString).thenReturn("MockedSessionCacheService")
      when(mockSessionCacheService.get[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId))(any[ClassTag[LookupResponse]],any(), any(), any()))
        .thenReturn(Future.successful(Some(response)))

      val result = service.getLookupFromCache(mockRequest, execute).value.futureValue

      result shouldBe Some(lookupObj)
    }

    "return None if cache ID in session is invalid" in {
      when(mockRequest.session).thenReturn(Session(Map("cacheId" -> invalidCacheId)))

      val result = service.getLookupFromCache(mockRequest, execute).value.futureValue

      result shouldBe None
    }

    "retrieve lookup response from cache if cache ID is valid" in {

      when(mockSessionCacheService.toString).thenReturn("MockedSessionCacheService")

      when(mockRequest.session).thenReturn(Session(Map("cacheId" -> cacheId)))

      when(mockSessionCacheService.get[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId))(any[ClassTag[LookupResponse]], any[HeaderCarrier], any(), any[OFormat[LookupResponse]]))
        .thenReturn(Future.successful(Some(lookupResponse)))


      val result = service.getLookupResponseFromCache(mockRequest, hc, execute).value.futureValue

      result shouldBe Some(lookupResponse)
    }

    "return None when lookup response is missing in cache" in {
      when(mockRequest.session).thenReturn(Session(Map("cacheId" -> cacheId)))

      when(mockSessionCacheService.get[LookupResponse](eqTo(cacheId), eqTo(service.responseCacheId))(any[ClassTag[LookupResponse]],any(), any(), any()))
        .thenReturn(Future.successful(None))

      val result = service.getLookupResponseFromCache(mockRequest, hc, execute).value.futureValue

      result shouldBe None
    }

    "format processingDate using both YYYY and yyyy to validate correct year behavior" in {

      val formatterYYYY = DateTimeFormatter.ofPattern("d MMMM YYYY 'at' h:mma '(BST).'")
      val formatteryyyy = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' h:mma '(BST).'")

      val processingDate = ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZoneId.of("Europe/London"))

      val resultWithYYYY = processingDate.format(formatterYYYY)
      val resultWithyyyy = processingDate.format(formatteryyyy)

//      println(s"Formatted date with 'YYYY': $resultWithYYYY")
//      println(s"Formatted date with 'yyyy': $resultWithyyyy")

      resultWithYYYY should include("31 December 2025") // Demonstrates the issue with "YYYY"
      resultWithyyyy should include("31 December 2024") // Validates the fix with "yyyy"
    }

  }
}
