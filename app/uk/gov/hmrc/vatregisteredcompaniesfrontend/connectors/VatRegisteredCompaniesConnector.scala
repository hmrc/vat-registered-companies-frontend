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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.connectors

import javax.inject.Inject
import play.api.{Configuration, Environment}
import play.api.Mode.Mode
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.{Lookup, LookupResponse, _}

import scala.concurrent.{ExecutionContext, Future}

class VatRegisteredCompaniesConnector @Inject()(
  http: HttpClient,
  environment: Environment,
  configuration: Configuration
) extends ServicesConfig {


  lazy val url: String = s"${baseUrl("vat-registered-companies")}/vat-registered-companies"

  override protected def mode: Mode = environment.mode
  override protected def runModeConfiguration: Configuration = configuration

  def lookup(lookup: Lookup)
    (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Some[LookupResponse]] = lookup match {
    case a: Lookup if a.requester.nonEmpty =>
      http.GET[LookupResponse](url = s"$url/lookup/${a.target.clean}/${a.requester.getOrElse("")}").map(Some(_))
    case a =>
      http.GET[LookupResponse](url = s"$url/lookup/${a.target.clean}").map(Some(_))
  }


}
