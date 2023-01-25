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

package uk.gov.hmrc.vatregisteredcompaniesfrontend

import cats.data.OptionT
import cats.implicits._
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, Request}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatregisteredcompaniesfrontend.connectors.VatRegisteredCompaniesConnector
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.{Lookup, LookupResponse, VatNumber}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.services.SessionCacheService
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.LookupResponse._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

@Singleton
class VatRegisteredCompaniesService @Inject()(sessionCacheService: SessionCacheService,
                                              connector: VatRegisteredCompaniesConnector) {
  lazy val responseCacheId = "responseCache"

  def getCacheId(lookup: Lookup): String = {
//    val basicKey = lookup.requester match {
//      case Some(requester) => s"TARGET_${lookup.target}_REQUESTER_${requester}"
//      case _ => s"TARGET_${lookup.target}"
//    }
//    val withOrWithoutConsultationNumber = {
//      if (lookup.withConsultationNumber) {
//        "_WITH_CONSULTATION_NUMBER"
//      } else {
//        "_WITHOUT_CONSULTATION_NUMBER"
//      }
//    }
//    basicKey + withOrWithoutConsultationNumber
    Json.toJson(lookup).toString()
  }

  def lookupVatComp(lookup: Lookup)(implicit hc: HeaderCarrier,
                                    ec: ExecutionContext): Future[LookupResponse] = {
    val cacheId = getCacheId(lookup)
    sessionCacheService.get[LookupResponse](cacheId, responseCacheId).flatMap{
      case Some(lookupResponse) =>
        Future.successful(lookupResponse)
      case None => connector.lookup(lookup).map { lookupResponse =>
        sessionCacheService.put[LookupResponse](cacheId, responseCacheId, lookupResponse)
        lookupResponse
      }
    }
  }

  def getLookupFromCache(implicit request: Request[AnyContent], hc: HeaderCarrier,
                         ec: ExecutionContext): OptionT[Future, Lookup] = for {
    cacheId <- OptionT.fromOption[Future](request.session.get("cacheId"))
    lookup <- OptionT.fromOption[Future](getLookupFromCacheId(cacheId))
  } yield lookup

  def getLookupResponseFromCache(implicit request: Request[AnyContent], hc: HeaderCarrier,
                                 ec: ExecutionContext): OptionT[Future, LookupResponse] = for {
    cacheId <- OptionT.fromOption[Future](request.session.get("cacheId"))
    lookup <- OptionT(sessionCacheService.get[LookupResponse](cacheId, responseCacheId))
  } yield lookup

  private def getLookupFromCacheId(cacheId: String): Option[Lookup] = {
    Try {
      Json.parse(cacheId).as[Lookup]
    } match {
      case Success(lookup) => Some(lookup)
      case _ => None
    }
  }
}
