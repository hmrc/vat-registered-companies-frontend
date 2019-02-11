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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.models

import java.time.{LocalDateTime, ZoneId}

import play.api.libs.json._

case class LookupResponse(
  target: Option[VatRegisteredCompany],
  requester: Option[VatNumber] = None,
  consultationNumber: Option[ConsultationNumber] = None,
  processingDate: ProcessingDate = LocalDateTime.now(ZoneId.of("Europe/London"))
)

object LookupResponse {
  //implicit val lookupResponseFormat: OFormat[LookupResponse] = Json.format[LookupResponse]

   implicit val lookupResponseFormat: Format[LookupResponse] = new Format[LookupResponse] {
    override def writes(o: LookupResponse): JsValue = Json.obj("target" -> o.target, "requester" -> o.requester, "consultationNumber" -> o.consultationNumber, "processingDate" -> o.processingDate)

    override def reads(json: JsValue): JsResult[LookupResponse] = {
      for {
        target <- (json \ "target").validate[VatRegisteredCompany]
        requester <- (json \ "requester").validate[VatNumber]
        consultationNumber <- (json \ "requester").validate[VatNumber]
        processingDate <- (json \ "requester").validate[ProcessingDate]
      } yield {
        LookupResponse(Some(target), Some(requester), Some(consultationNumber), processingDate )
      }
    }
  }

}
