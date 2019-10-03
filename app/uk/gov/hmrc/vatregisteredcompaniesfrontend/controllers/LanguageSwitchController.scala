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
import play.api.Mode.Mode
import play.api.{Configuration, Environment}
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call}
import uk.gov.hmrc.play.bootstrap.config.RunMode
import uk.gov.hmrc.play.language.{LanguageController, LanguageUtils}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig

class LanguageSwitchController @Inject()(
                                          override implicit val messagesApi: MessagesApi,
                                          configuration: Configuration,
                                          environment: Environment,
                                          appConfig: AppConfig,
                                          runMode: RunMode,
  languageUtils: LanguageUtils)
  extends LanguageController(configuration, languageUtils) {

  override def languageMap: Map[String, Lang] = appConfig.languageMap

  override protected def fallbackURL: String = routes.VatRegCoLookupController.lookupForm().url

//  override protected def mode: Mode = environment.mode
//
//  override protected def runModeConfiguration: Configuration = configuration
}