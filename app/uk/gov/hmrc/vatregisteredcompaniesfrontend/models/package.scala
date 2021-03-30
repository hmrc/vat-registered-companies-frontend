/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.vatregisteredcompaniesfrontend

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import play.api.i18n.Messages

package object models {

  type CompanyName = String
  type VatNumber = String
  type ConsultationNumber = String
  type ProcessingDate = ZonedDateTime

  /* removes "GB" from the VatNumber, used by requests to the BE only */
  implicit class RichVatNumber(val self: VatNumber) {
    def clean: VatNumber = self.replace("GB", "")
  }

  implicit class RichProcessingDate(val self: ProcessingDate) {

    override def toString: String = self.format(DateTimeFormatter.ofPattern("h:mma"))

    def formatWithWelsh(pattern: String)(implicit messages: Messages): String = {
      self.format(
        DateTimeFormatter.ofPattern(
          if (messages.lang.code == "cy")
            pattern.replace(
              "MMMM",
              "'"+messages("vatcheck.month."+self.getMonthValue)+"'"
            ).replace("at", "am")
          else pattern
        )
      )
    }

  }
}
