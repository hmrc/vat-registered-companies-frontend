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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.models

import play.api.libs.json.{Json, OFormat}

case class Address(
  line1: String,
  line2: Option[String],
  line3: Option[String],
  line4: Option[String],
  line5: Option[String],
  postcode: Option[String],
  countryCode: String
) {
  def lines: List[String] = {
    line1 +: List(
      line2,
      line3,
      line4,
      line5,
      postcode
    ).collect { case Some(str) =>
      str
    } :+ countryCode
  }
}

object Address {
  implicit val addressFormat: OFormat[Address] =
    Json.format[Address]
}
