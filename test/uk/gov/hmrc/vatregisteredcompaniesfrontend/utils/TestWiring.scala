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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.utils

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.concurrent.Execution.defaultContext
import uk.gov.hmrc.vatregisteredcompaniesfrontend.connectors.VatRegisteredCompaniesConnector

import scala.concurrent.{ExecutionContext, Future}

trait TestWiring extends MockitoSugar {

  implicit val ec: ExecutionContext = defaultContext

  lazy val mockVatRegCoConnector: VatRegisteredCompaniesConnector = {
    val mymock = mock[VatRegisteredCompaniesConnector]

    when(mymock.lookup(any())(any(), any())).thenReturn(Future.successful(Some((any()))))
    mymock
  }

}
