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

import cats.data.OptionT
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.{reset, when}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.api.mvc.{Action, AnyContent}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import org.scalatestplus.play.BaseOneAppPerSuite
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.filters.IpRateLimitFilter
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.{ConsultationNumber, Lookup, *}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.services.SessionCacheService
import uk.gov.hmrc.vatregisteredcompaniesfrontend.utils.BaseSpec
import org.scalatest.Assertion
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}
import scala.concurrent.Future

class VatRegCoLookupControllerSpec extends BaseSpec with BaseOneAppPerSuite {

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

  override lazy val app: Application = buildAppConfig()
  val controller: VatRegCoLookupController = buildController(app)

  def buildAppConfig(rateLimitEnabled: Boolean = true): Application = new GuiceApplicationBuilder()
    .configure(
      "metrics.enabled" -> "false",
      "filters.rateLimit.enabled" -> rateLimitEnabled,
      "filters.rateLimit.bucketSize" -> 10
    )
    .build()

  def buildController(app: Application): VatRegCoLookupController = {
    new VatRegCoLookupController(
      mockVatRegCoService,
      mcc,
      lookupPage,
      invalidVatNumberPage,
      confirmationPage,
      new IpRateLimitFilter(app.injector.instanceOf[AppConfig])
    )
  }

  def verifyRateLimitingActions(controller: VatRegCoLookupController, assertion: Seq[Int] => Assertion): Unit = {
    when(mockVatRegCoService.getLookupFromCache(any(), any())).thenReturn(OptionT.none[Future, Lookup])

    val actions: Seq[Action[AnyContent]] = Seq(
      controller.unknownWithoutConsultationNumber,
      controller.unknownWithValidConsultationNumber,
      controller.unknownWithInvalidConsultationNumber
    )

    actions.foreach { action =>
      val statuses = for (_ <- 0 until 100) yield status(action()(fakeRequest))
      assertion(statuses)
    }
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

      val request = FakeRequest("POST", "/enter-vat-details")
        .withFormUrlEncodedBody("target" -> testVatNumber, "withConsultationNumber" -> boolValue.toString, "requester" -> requesterVatNo.getOrElse(""))

      val lookupResponseObj = new LookupResponse(Some(vatRegCompany), requesterVatNo, Some(new ConsultationNumber("Consul9999")), ZonedDateTime.of(LocalDateTime.now,ZoneId.of("Europe/London")))

      when(mockVatRegCoService.lookupVatComp(any())(any(), any())).thenReturn {
       Future.successful(lookupResponseObj)
      }

      when(mockVatRegCoService.getCacheId(any())).thenReturn {
        "lookupCacheKey"
      }

      val result = controller.submit()(request)

      status(result) shouldBe Status.SEE_OTHER

      reset(mockVatRegCoService)
    }

    "return OK and get invalid Reg No for a non-existing VAT No for a POST" in {

      val request = FakeRequest("POST", "/enter-vat-details")
        .withFormUrlEncodedBody(
          "target" -> testVatNumber,
          "withConsultationNumber" -> boolValue.toString,
          "requester" -> requesterVatNo.getOrElse(""))
      val lookupResponseObj = new LookupResponse(None, requesterVatNo, Some(new ConsultationNumber("Consul9999")), ZonedDateTime.of(LocalDateTime.now,ZoneId.of("Europe/London")))


      when(mockVatRegCoService.lookupVatComp(any())(any(), any())).thenReturn {
        Future.successful(lookupResponseObj)
      }

      when(mockVatRegCoService.getCacheId(any())).thenReturn {
        "lookupCacheKey"
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

    "redirect to lookup form with Welsh language for cymraeg action" in {
      val result = controller.cymraeg()(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.VatRegCoLookupController.lookupForm.url)
      cookies(result).get("PLAY_LANG").map(_.value) shouldBe Some("cy")
    }


    "redirect to appropriate routes for unknownWithInvalidConsultationNumber" in {
      when(mockVatRegCoService.getLookupFromCache(any(), any())).thenReturn(OptionT.none[Future, Lookup])

      val result = controller.unknownWithInvalidConsultationNumber()(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.VatRegCoLookupController.lookupForm.url)
    }

    "redirect to appropriate routes for unknownWithValidConsultationNumber" in {
      when(mockVatRegCoService.getLookupFromCache(any(), any())).thenReturn(OptionT.none[Future, Lookup])

      val result = controller.unknownWithValidConsultationNumber()(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.VatRegCoLookupController.lookupForm.url)
    }

    "redirect to appropriate routes for unknownWithoutConsultationNumber" in {
      when(mockVatRegCoService.getLookupFromCache(any(), any())).thenReturn(OptionT.none[Future, Lookup])

      val result = controller.unknownWithoutConsultationNumber()(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.VatRegCoLookupController.lookupForm.url)
    }

    "return TOO_MANY_REQUESTS when too many unknown VAT lookup requests are made" in {
      verifyRateLimitingActions(controller, statuses => atLeast(1, statuses) shouldBe Status.TOO_MANY_REQUESTS)
    }

    "return SEE_OTHER when rate limiting is disabled for unknown VAT lookup requests" in {
      val appWithRateLimitDisabled = buildAppConfig(rateLimitEnabled = false)
      val controllerWithNoRateLimit = buildController(appWithRateLimitDisabled)

      verifyRateLimitingActions(controllerWithNoRateLimit, statuses => all(statuses) shouldBe SEE_OTHER)
    }

    "return the known page with valid consultation number" in {
      when(mockVatRegCoService.getLookupFromCache(any(), any())).thenReturn(OptionT[Future, Lookup](Future.successful(Some(lookupObj))))
      when(mockVatRegCoService.getLookupResponseFromCache(any(), any(), any())).thenReturn(
        OptionT.some[Future](LookupResponse(Some(vatRegCompany), requesterVatNo, Some(new ConsultationNumber("Valid")), ZonedDateTime.now()))
      )

      val result = controller.knownWithValidConsultationNumber()(fakeRequest)

      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
      contentAsString(result) should include("XYZ Exports")
    }

    "return the known page with invalid consultation number" in {
      when(mockVatRegCoService.getLookupFromCache(any(), any())).thenReturn(OptionT[Future, Lookup](Future.successful(Some(lookupObj))))
      when(mockVatRegCoService.getLookupResponseFromCache(any(), any(), any())).thenReturn(
        OptionT.some[Future](LookupResponse(Some(vatRegCompany), requesterVatNo, Some(new ConsultationNumber("Invalid")), ZonedDateTime.now()))
      )

      val result = controller.knownWithInvalidConsultationNumber()(fakeRequest)

      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
      contentAsString(result) should include("XYZ Exports")
    }

    "return the known page without consultation number" in {
      when(mockVatRegCoService.getLookupFromCache(any(), any())).thenReturn(OptionT[Future, Lookup](Future.successful(Some(lookupObj))))
      when(mockVatRegCoService.getLookupResponseFromCache(any(), any(), any())).thenReturn(
        OptionT.some[Future](LookupResponse(Some(vatRegCompany), requesterVatNo, None, ZonedDateTime.now()))
      )

      val result = controller.knownWithoutConsultationNumber()(fakeRequest)

      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
      contentAsString(result) should include("XYZ Exports")
    }

  }

}
