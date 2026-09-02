/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.forms

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers.VatRegCoLookupController.form

class VatNumberFormSpec extends AnyWordSpec with Matchers {

  "VAT number form mapping" should {

    "accept 9 digits unchanged" in {
      val result = form.bind(Map("target" -> "123456789", "withConsultationNumber" -> "false"))
      result.value.get.target shouldBe "123456789"
    }

    "accept 12 digits unchanged" in {
      val result = form.bind(Map("target" -> "123456789000", "withConsultationNumber" -> "false"))
      result.value.get.target shouldBe "123456789000"
    }

    "strip GB prefix (uppercase)" in {
      val result = form.bind(Map("target" -> "GB123456789", "withConsultationNumber" -> "false"))
      result.value.get.target shouldBe "123456789"
    }

    "strip gb prefix (lowercase)" in {
      val result = form.bind(Map("target" -> "gb123456789", "withConsultationNumber" -> "false"))
      result.value.get.target shouldBe "123456789"
    }

    "remove whitespace inside and around VAT number" in {
      Seq(" 123 456 789 ", " GB 123 456 789 ", "123 456 789", "GB123 456789", " 123\u00A0456\u00A0789 ", " GB\u00A0123\u00A0456\u00A0789 ").foreach { vat =>
        val result = form.bind(Map("target" -> vat, "withConsultationNumber" -> "false"))
        result.value.get.target shouldBe "123456789"
      }
    }

    "reject invalid prefixes" in {
      Seq("GG123456789", "BB123456789", "Bg123456789", "XI123456789").foreach { vat =>
        val result = form.bind(Map("target" -> vat, "withConsultationNumber" -> "false"))
        result.errors.map(_.message) should contain("error.target.invalid")
      }
    }

    "reject values not 9 or 12 digits after normalisation" in {
      Seq("123", "12345678", "1234567891", "1234567891234").foreach { vat =>
        val result = form.bind(Map("target" -> vat, "withConsultationNumber" -> "false"))
        result.errors.map(_.message) should contain("error.target.invalid")
      }
    }

    "reject duplicate GB prefix" in {
      val result = form.bind(Map("target" -> "GBGB123456789", "withConsultationNumber" -> "false"))
      result.errors.map(_.message) should contain("error.target.invalid")

      val requesterResult = form.bind(Map("target" -> "123456789", "withConsultationNumber" -> "true", "requester" -> "GBGB123456789"))
      requesterResult.errors.map(_.message) should contain("error.requester.invalid")
    }

    "apply same normalisation to requester VAT number" in {
      val result = form.bind(Map("target" -> "GB123456789", "withConsultationNumber" -> "true", "requester" -> "gb999999999"))
      result.value.get.requester.get shouldBe "999999999"
    }
  }
}
