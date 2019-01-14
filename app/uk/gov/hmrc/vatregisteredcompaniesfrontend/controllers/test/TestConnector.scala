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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers.test

import javax.inject.Inject
import play.api.Mode.Mode
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

class TestConnector @Inject()(
  http: HttpClient,
  environment: Environment,
  configuration: Configuration
) extends ServicesConfig {


  private val backendUrl: String = baseUrl("vat-registered-companies")

  def trigger(url: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    http.GET(s"$backendUrl/test-only/$url")

  override protected def mode: Mode = environment.mode
  override protected def runModeConfiguration: Configuration = configuration
}
