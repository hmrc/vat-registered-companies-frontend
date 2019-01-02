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
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.ShortLivedHttpCaching
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.uniform.webmonad._
import uk.gov.hmrc.vatregisteredcompaniesfrontend.uniform.FakePersistence

import scala.concurrent.{ExecutionContext, Future}

class VatRegCoLookupController @Inject()(
  val messagesApi: MessagesApi,
  cache: ShortLivedHttpCaching
)(
  implicit val ec: ExecutionContext
) extends WebMonadController with FrontendController {


  def program(id: String)(implicit hc: HeaderCarrier): WebMonad[Result] = ???

  def index(id: String): Action[AnyContent] = Action.async { implicit request =>
    val key = request.session("uuid")
    val persistence = FakePersistence()
    runInner(request)(program(id))(id)(persistence.dataGet, persistence.dataPut)
  }

  /*

    def index(id: String): Action[AnyContent] = authorisedAction.async { implicit request =>
      val persistence = SaveForLaterPersistence("registration", request.internalId, cache.shortLiveCache)
      cache.get(request.internalId) flatMap {
        case Some(fd) => runInner(request)(program(fd)(request, implicitly))(id)(persistence.dataGet,persistence.dataPut)
        case None => NotFound("").pure[Future]
      }
    }


   */

}
