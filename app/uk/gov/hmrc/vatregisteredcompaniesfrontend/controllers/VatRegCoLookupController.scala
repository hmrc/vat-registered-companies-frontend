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

import cats.implicits._
import com.google.inject.Singleton

import javax.inject.Inject
import play.api.data.Forms.{boolean, mapping, text}
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.data.{Form, Mapping}
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.vatregisteredcompaniesfrontend.filters.IpRateLimitFilter
import uk.gov.hmrc.vatregisteredcompaniesfrontend.VatRegisteredCompaniesService
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.Lookup
import views.html.vatregisteredcompaniesfrontend.*

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatRegCoLookupController @Inject()(
  service: VatRegisteredCompaniesService,
  mcc: MessagesControllerComponents,
  lookupPage: LookupPage,
  invalidVatNumberPage: InvalidVatNumberPage,
  confirmationPage: ConfirmationPage,
  ipRateLimitFilter: IpRateLimitFilter
)(implicit ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

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
      Future.successful(Ok(lookupPage(form)))
  }

  def submit: Action[AnyContent] = Action.async { implicit request =>
    form.bindFromRequest().fold(
      errors => Future(BadRequest(lookupPage(errors))),
      lookup => {
        val redirectLocation = service.lookupVatComp(lookup) map {
        case response if response.target.isEmpty & lookup.withConsultationNumber & response.requester.nonEmpty =>
          routes.VatRegCoLookupController.unknownWithValidConsultationNumber
        case response if response.target.isEmpty & lookup.withConsultationNumber =>
          routes.VatRegCoLookupController.unknownWithInvalidConsultationNumber
        case response if response.target.isEmpty =>
          routes.VatRegCoLookupController.unknownWithoutConsultationNumber
        case response if lookup.withConsultationNumber & response.requester.nonEmpty =>
          routes.VatRegCoLookupController.knownWithValidConsultationNumber
        case _ if lookup.withConsultationNumber =>
          routes.VatRegCoLookupController.knownWithInvalidConsultationNumber
        case _ => routes.VatRegCoLookupController.knownWithoutConsultationNumber

      }
        redirectLocation.map(Redirect(_).withSession(request.session + ("cacheId" -> service.getCacheId(lookup))))
      }
    )
  }

  private def unknown(implicit request: Request[AnyContent]) =
    service.getLookupFromCache.fold(Redirect(routes.VatRegCoLookupController.lookupForm)) { x =>
      Ok(invalidVatNumberPage(x.target, x.withConsultationNumber))
    }

  private def known(implicit request: Request[AnyContent]) = {
    val x = for {
      l <- service.getLookupFromCache
      r <- service.getLookupResponseFromCache
    } yield (l, r)
    x.fold(Redirect(routes.VatRegCoLookupController.lookupForm)) { x =>
      Ok(confirmationPage(x._2, x._1.withConsultationNumber))
    }
  }

  def unknownWithInvalidConsultationNumber: Action[AnyContent] = (Action andThen ipRateLimitFilter).async { implicit request =>
    unknown
  }

  def unknownWithValidConsultationNumber: Action[AnyContent] = (Action andThen ipRateLimitFilter).async { implicit request =>
    unknown
  }

  def unknownWithoutConsultationNumber: Action[AnyContent] = (Action andThen ipRateLimitFilter).async { implicit request =>
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

  private val vatNoRegex: String = "^[0-9]{9}|[0-9]{12}|[gbGB]{2}[0-9]{9}|[gbGB]{2}[0-9]{12}$"

  val form: Form[Lookup] = Form(
    mapping(
      "target" -> mandatoryVatNumber("target"),
      "withConsultationNumber" -> boolean,
      "requester" -> mandatoryIfTrue("withConsultationNumber", mandatoryVatNumber("requester"))
    )(Lookup.apply)(o => Some(Tuple.fromProductTyped(o)))
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
    text.transform[String](_.replace(" ", "").replace("[gbGB]{2}", ""), s => s).verifying(combine(required(key),vatNumberConstraint(key)))
  }

}

