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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers.test

import javax.inject.Inject
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.{ExecutionContext, Future}

class TestConnector @Inject()(
                               http: HttpClientV2,
                               servicesConfig: ServicesConfig
) {

  private val backendUrl: String = servicesConfig.baseUrl("vat-registered-companies")

  def trigger(url: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    http.get(url"$backendUrl/test-only/$url").execute[HttpResponse]

  def trigger(url: String, param: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    http.get(url"$backendUrl/test-only/$url/$param").execute[HttpResponse]

}
