/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.Environment
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig

class LanguageSwitchController @Inject()(
  messagesControllerComponents: MessagesControllerComponents,
  val appConfig: AppConfig,
  environment: Environment
) extends FrontendController(messagesControllerComponents) with I18nSupport {

  def langToCall(lang: String): Call = routes.LanguageSwitchController.switchToLanguage(lang)

  def switchToLanguage(language: String): Action[AnyContent] = Action { implicit request =>
    val enabled = appConfig.languageTranslationEnabled
    val lang =
      if (enabled) languageMap.getOrElse(language, LanguageUtils.getCurrentLang)
      else Lang("en")
    val redirectURL = request.headers.get(REFERER).getOrElse(fallbackURL)
    Redirect(redirectURL)
      .withLang(Lang.apply(lang.code))
      .flashing(LanguageUtils.FlashWithSwitchIndicator)
  }

  def fallbackURL: String = routes.VatRegCoLookupController.lookupForm().url

  def languageMap: Map[String, Lang] = appConfig.languageMap
}