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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.repo

import cats.data.OptionT
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.http.{HeaderCarrier}
import uk.gov.hmrc.mongo.{CurrentTimestampSupport, MongoComponent}
import uk.gov.hmrc.mongo.cache.{CacheIdType, DataKey, MongoCacheRepository}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

@ImplementedBy(classOf[SessionStoreImpl])
trait SessionStore {

  def getSession[A : ClassTag](sessionId: String, key: String)(implicit hc: HeaderCarrier, ec: ExecutionContext, reads: Reads[A]): OptionT[Future, A]

  def putSession[A](sessionId: String, key: String, data: A )(implicit hc: HeaderCarrier, ec: ExecutionContext, writes: Writes[A]): Future[A]

}
object SessionIdType extends CacheIdType[String] {
  def run: String => String = identity
}

@Singleton
class SessionStoreImpl @Inject()(mongo: MongoComponent)(implicit appConfig: AppConfig, ec: ExecutionContext) extends
  MongoCacheRepository[String](
    mongoComponent = mongo,
    collectionName = "sessions",
    ttl = appConfig.mongoSessionExpireAfter,
    timestampSupport = new CurrentTimestampSupport(),
    cacheIdType = SessionIdType
  )
  with SessionStore {

  private def dataKeyForType[A](key: String) = {
    DataKey[A](key)
  }

  override def getSession[A : ClassTag](sessionId: String, key: String)(implicit hc: HeaderCarrier, ec: ExecutionContext, reads: Reads[A]): OptionT[Future, A] = {
  OptionT {
      get[A](sessionId)(dataKeyForType(key))
    }
  }

  override def putSession[A](sessionId: String, key: String, data: A)(implicit hc: HeaderCarrier, ec: ExecutionContext, writes: Writes[A]): Future[A] = {

   put[A](sessionId)(DataKey[A](key), data).map(_ => data)

  }

}