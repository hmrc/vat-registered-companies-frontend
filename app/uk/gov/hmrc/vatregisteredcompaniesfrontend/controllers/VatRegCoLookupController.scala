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
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.{Lookup, LookupResponse}
import views.html.vatregisteredcompaniesfrontend._

import scala.concurrent.Future

class VatRegCoLookupController @Inject()(
  val messagesApi: MessagesApi,
  connector: VatRegisteredCompaniesConnector,
  implicit val config: AppConfig
) extends FrontendController with I18nSupport {

  import VatRegCoLookupController.form

  def start: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(views.html.vatregisteredcompaniesfrontend.start_page()))
  }

  def lookupForm: Action[AnyContent] = Action.async {implicit request =>
    Future.successful(Ok(lookup(form)))
  }

  def submit: Action[AnyContent] = Action.async { implicit request =>
    form.bindFromRequest().fold(
      errors => Future(BadRequest(lookup(errors))),
      lookup => connector.lookup(lookup) flatMap  {
        case Some(response: LookupResponse) if response.target.isEmpty =>
          Future.successful(Ok(invalid_vat_number(response, lookup.target)))
        case Some(response: LookupResponse) =>
          Future.successful(Ok(confirmation(response, lookup.withConsultationNumber)))
      }
    )
  }
}

object VatRegCoLookupController {

  import uk.gov.voa.play.form.ConditionalMappings._

  private val vatNoRegex: String = "^[0-9]{9}|[0-9]{12}|GB[0-9]{9}|GB[0-9]{12}$"

  val form: Form[Lookup] = Form(
    mapping(
      "target" -> mandatoryVatNumber("target"),
      "withConsultationNumber" -> boolean,
      "requester" -> mandatoryIfTrue("withConsultationNumber", mandatoryVatNumberAndNotEqual("requester", "target"))
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
    println(s"YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY $key")
    text.transform[String](_.trim, s => s).verifying(combine(required(key),vatNumberConstraint(key)))
  }

  private def notEqual(key: String, other: String): Constraint[String] = {
    println(s"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX $key $other")
    Constraint {
      case a if key == other => println(s"ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ $a");Invalid("error.$key.same-as-other")
      case b => println(s"VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV $b");Valid
    }
  }

  private def mandatoryVatNumberAndNotEqual(key: String, other: String): Mapping[String] = {
    text.transform[String](_.trim, s => s).verifying(
      combine(required(key),combine(vatNumberConstraint(key), notEqual(key, other)))
    )
  }

}

