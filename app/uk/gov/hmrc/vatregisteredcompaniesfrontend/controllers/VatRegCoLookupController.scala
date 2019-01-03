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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers

import javax.inject.Inject
import play.api.data.Forms.{boolean, mapping, text}
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.data.{Form, Mapping}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.connectors.VatRegisteredCompaniesConnector
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.Lookup
import views.html.vatregisteredcompaniesfrontend._

import scala.concurrent.Future

class VatRegCoLookupController @Inject()(
  val messagesApi: MessagesApi,
  connector: VatRegisteredCompaniesConnector,
  implicit val config: AppConfig
) extends FrontendController with I18nSupport {

  import VatRegCoLookupController.form

  def start: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(start()))
  }

  def lookupForm: Action[AnyContent] = Action.async {implicit request =>
    Future.successful(Ok(lookup(form)))
  }

  def submit = Action.async { implicit request =>
    form.bindFromRequest().fold(
      errors => BadRequest(lookup(errors)),
      lookup => connector.lookup(lookup) map { x =>
        ???
      }
    )
  }

}

object VatRegCoLookupController {

  import uk.gov.voa.play.form.ConditionalMappings._

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
    case a if !a.matches("^[0-9]{9}|[0-9]{12}$") => Invalid(s"error.$key.invalid")
  }

  private def mandatoryVatNumber(key: String): Mapping[String] = {
    text.transform[String](_.trim, s => s).verifying(combine(required(key),vatNumberConstraint(key)))
  }

  val form: Form[Lookup] = Form(
    mapping(
      "target" -> mandatoryVatNumber("target"),
      "withConsultationNumber" -> boolean,
      "requester" -> mandatoryIfTrue("withConsultationNumber", mandatoryVatNumber("requester"))
    )(Lookup.apply)(Lookup.unapply)
  )

}

