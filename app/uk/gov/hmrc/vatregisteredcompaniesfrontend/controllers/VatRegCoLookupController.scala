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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers

import cats.data.OptionT
import cats.implicits._
import com.google.inject.Singleton
import javax.inject.Inject
import play.api.data.Forms.{boolean, mapping, text}
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.data.{Form, Mapping}
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.connectors.VatRegisteredCompaniesConnector
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.{Lookup, LookupResponse}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.services.SessionCacheService
import views.html.vatregisteredcompaniesfrontend._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatRegCoLookupController @Inject()(
  connector: VatRegisteredCompaniesConnector,
  cache: SessionCacheService,
  mcc: MessagesControllerComponents,
  lookupPage: LookupPage,
  invalidVatNumberPage: InvalidVatNumberPage,
  confirmationPage: ConfirmationPage
)(implicit config: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  import VatRegCoLookupController.form

  def cymraeg: Action[AnyContent] = Action.async {
    Future.successful(
      Redirect(
        routes.VatRegCoLookupController.lookupForm
      ).withLang(
        Lang.apply("cy")
      )
    )
  }

  def lookupForm: Action[AnyContent] = Action.async { implicit request =>
    cache.sessionUuid(request).fold {
      Redirect(routes.VatRegCoLookupController.lookupForm)
        .withSession(request.session + ("uuid" -> java.util.UUID.randomUUID.toString)).pure[Future]
    } { _ =>
      Future.successful(Ok(lookupPage(form)))
    }
  }

  val lookupCacheId = "lookupCache"
  val responseCacheId = "responseCache"

  def cacheLookup(sessionId: String, lookup: Lookup)(implicit request: Request[AnyContent]): Future[Boolean] =
    cache.put[Lookup](sessionId, lookupCacheId, lookup)

  def cacheResponse(sessionId: String, response: LookupResponse)(implicit request: Request[AnyContent]): Future[Boolean] =
    cache.put[LookupResponse](sessionId, responseCacheId, response)

  def submit: Action[AnyContent] = Action.async { implicit request =>
    form.bindFromRequest().fold(
      errors => Future(BadRequest(lookupPage(errors))),
      lookup => connector.lookup(lookup) flatMap { x =>
        cache.sessionUuid(request).fold {
          Redirect(routes.VatRegCoLookupController.submit)
            .withSession(request.session + ("uuid" -> java.util.UUID.randomUUID.toString)).pure[Future]
        } { sessionId =>
          cacheLookup(sessionId, lookup).flatMap{ _ =>
            x match {
              case Some(response: LookupResponse)
                if response.target.isEmpty & lookup.withConsultationNumber & response.requester.nonEmpty
              =>
                cacheResponse(sessionId, response).map { _ =>
                  Redirect(routes.VatRegCoLookupController.unknownWithValidConsultationNumber)
                }
              case Some(response: LookupResponse)
                if response.target.isEmpty & lookup.withConsultationNumber
              =>
                cacheResponse(sessionId, response).map { _ =>
                  Redirect(routes.VatRegCoLookupController.unknownWithInvalidConsultationNumber)
                }
              case Some(response: LookupResponse)
                if response.target.isEmpty
              =>
                cacheResponse(sessionId, response).map { _ =>
                  Redirect(routes.VatRegCoLookupController.unknownWithoutConsultationNumber)
                }
              case Some(response: LookupResponse)
                if lookup.withConsultationNumber & response.requester.nonEmpty
              =>
                cacheResponse(sessionId, response).map { _ =>
                  Redirect(routes.VatRegCoLookupController.knownWithValidConsultationNumber)
                }
              case Some(response: LookupResponse)
                if lookup.withConsultationNumber
              =>
                cacheResponse(sessionId, response).map { _ =>
                  Redirect(routes.VatRegCoLookupController.knownWithInvalidConsultationNumber)
                }
              case Some(response: LookupResponse)
              =>
                cacheResponse(sessionId, response).map{ _ =>
                  Redirect(routes.VatRegCoLookupController.knownWithoutConsultationNumber)
                }
              case _ => throw new MissingLookupResponseException
            }
          }
        }
      }
    )
  }

  def getLookupResponseFromCache(implicit request: Request[AnyContent]): OptionT[Future, LookupResponse] = for {
    sessionId <- OptionT.fromOption[Future](request.session.get("uuid"))
    lookup <- OptionT(cache.get[LookupResponse](sessionId, responseCacheId))
  } yield lookup

  def getLookupFromCache(implicit request: Request[AnyContent]): OptionT[Future, Lookup] = for {
    sessionId <- OptionT.fromOption[Future](request.session.get("uuid"))
    lookup <- OptionT(cache.get[Lookup](sessionId, lookupCacheId))
  } yield lookup

  private def unknown(implicit request: Request[AnyContent]) =
    getLookupFromCache.fold(Redirect(routes.VatRegCoLookupController.lookupForm)) { x =>
      Ok(invalidVatNumberPage(x.target, x.withConsultationNumber))
    }

  private def known(implicit request: Request[AnyContent]) = {
    val x = for {
      l <- getLookupFromCache
      r <- getLookupResponseFromCache
    } yield (l, r)
    x.fold(Redirect(routes.VatRegCoLookupController.lookupForm)) { x =>
      Ok(confirmationPage(x._2, x._1.withConsultationNumber))
    }
  }

  def unknownWithInvalidConsultationNumber: Action[AnyContent] = Action.async { implicit request =>
    unknown
  }

  def unknownWithValidConsultationNumber: Action[AnyContent] = Action.async { implicit request =>
    unknown
  }

  def unknownWithoutConsultationNumber: Action[AnyContent] = Action.async { implicit request =>
    unknown
  }

  def knownWithValidConsultationNumber: Action[AnyContent] = Action.async { implicit request =>
    known
  }

  def knownWithInvalidConsultationNumber: Action[AnyContent] = Action.async { implicit request =>
    known
  }

  def knownWithoutConsultationNumber: Action[AnyContent] = Action.async { implicit request =>
    known
  }

  class MissingLookupResponseException extends RuntimeException("no LookupResponse from VatRegisteredCompaniesConnector")

}

object VatRegCoLookupController {

  import uk.gov.voa.play.form.ConditionalMappings._

  private val vatNoRegex: String = "^[0-9]{9}|[0-9]{12}|GB[0-9]{9}|GB[0-9]{12}$"

  val form: Form[Lookup] = Form(
    mapping(
      "target" -> mandatoryVatNumber("target"),
      "withConsultationNumber" -> boolean,
      "requester" -> mandatoryIfTrue("withConsultationNumber", mandatoryVatNumber("requester"))
    )(Lookup.apply)(Lookup.unapply)
  )

  private def combine[T](c1: Constraint[T], c2: Constraint[T]): Constraint[T] = Constraint { v =>
    c1.apply(v) match {
      case Valid => c2.apply(v)
      case i: Invalid => i
    }
  }

  private def required(key: String): Constraint[String] = Constraint {
    case "" => Invalid(s"error.$key.required")
    case _ => Valid
  }

  private def vatNumberConstraint(key: String): Constraint[String] = Constraint {
    case a if !a.matches(vatNoRegex) => Invalid(s"error.$key.invalid")
    case _ => Valid
  }

  private def mandatoryVatNumber(key: String): Mapping[String] = {
    text.transform[String](_.replace(" ", ""), s => s).verifying(combine(required(key),vatNumberConstraint(key)))
  }

}

