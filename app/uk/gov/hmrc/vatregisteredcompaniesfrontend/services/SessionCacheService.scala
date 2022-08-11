/*
 * Copyright 2022 HM Revenue & Customs
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

import cats.implicits._
import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.OFormat
import play.api.mvc.{AnyContent, Request}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.repo.SessionStore

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class SessionCacheService @Inject()(sessionStore: SessionStore) {

  val logger = Logger(getClass)

  def sessionUuid(request: Request[AnyContent]): Option[String] =  request.session.get("uuid")

  def get[A: ClassTag](
    cacheId:String,
    id: String
  )(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    format: OFormat[A]
  ): Future[Option[A]] = {
    sessionStore.getSession[A](cacheId, id).fold {
      logger.info(s"no ${scala.reflect.classTag[A].runtimeClass} in the cache")
      Option.empty[A]
    }(_.some)
  }

  def put[A](
    cacheId:String,
    id: String,
    data: A
  )(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    format: OFormat[A]
  ): Future[Boolean] = {
    sessionStore.putSession(cacheId, id, data)
      .map(_ => true)
      .recover
      { case e  => logger.error(
        s" Store session failed  for id :: $id and cache id :: $cacheId with error :: ${e.getMessage}", e)
      false}
  }

}
