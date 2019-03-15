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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.services

import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.OFormat
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.{HttpCaching, SessionCache}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig

import scala.concurrent.{ExecutionContext, Future}

class SessionCacheService @Inject()(appConfig: AppConfig, httpClient: HttpClient) extends SessionCache {

  override def defaultSource: String = appConfig.keyStoreSource

  override def baseUri: String = appConfig.keyStoreUrl

  override def domain: String = appConfig.sessionCacheDomain

  override def http: HttpClient = httpClient

  def get[A](cacheId:String, id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext, format: OFormat[A]): Future[Option[A]] = {
    fetchAndGetEntry[A](defaultSource, cacheId, id).recover {
      case ex: Throwable => Logger.error(s"cannot fetch  data from Session Cache => " +
        s"cacheId ($cacheId) , id : ${id} ,  \n Exception is ${ex.getMessage}" )
        None
    }
  }

  def put[A](cacheId:String, id: String, data: A)(implicit hc: HeaderCarrier, ec: ExecutionContext, format: OFormat[A]): Future[Boolean] = {
    cache(defaultSource, cacheId, id, data).map { res =>
      true
    }.recover {
      case ex: Throwable => Logger.error(s"cannot save  data to Session Cache => " +
        s"cacheId ($cacheId) , id : ${id} ,  \n Exception is ${ex.getMessage}" )
        throw new RuntimeException(s"Error in caching ${ex.getMessage}")
    }
  }

}
