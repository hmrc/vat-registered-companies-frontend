package uk.gov.hmrc.vatregisteredcompaniesfrontend.models

import play.api.libs.json.{Json, OFormat}

case class LookupResponse(
  target: VatRegisteredCompany,
  requester: Option[VatNumber] = None,
  consultationNumber: Option[ConsultationNumber] = None
)

object LookupResponse {
  implicit val lookupResponseFormat: OFormat[LookupResponse] = Json.format[LookupResponse]
}
