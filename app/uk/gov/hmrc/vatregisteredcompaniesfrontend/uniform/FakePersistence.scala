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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.uniform

import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.uniform.Persistence
import uk.gov.hmrc.http.cache.client.ShortLivedHttpCaching // TODO remove library from build.sbt

import scala.concurrent.{ExecutionContext, Future}

/*
 Just here to make Uniform happy
 */
case class FakePersistence (implicit
  ec: ExecutionContext
) extends Persistence {

  def dataGet(session: String): Future[Map[String, JsValue]] = Future(Map.empty[String, JsValue])

  def dataPut(session: String, dataIn: Map[String, JsValue]): Unit = Future.successful()

}

