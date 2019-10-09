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

package uk.gov.hmrc.vatregisteredcompaniesfrontend

import play.api.i18n.Lang
import play.api.test.FakeRequest
import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig

package object controllers {

  val env = Environment.simple()
  val configuration = Configuration.load(env)
  val fakeRequest = FakeRequest("GET", "/")
  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
  val cc = play.api.test.Helpers.stubControllerComponents()
  val mcc = uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents()
  val messagesApi = mcc.messagesApi
  implicit val lang = Lang.defaultLang
  val sc: ServicesConfig = new ServicesConfig(configuration, new RunMode(configuration, Mode.Dev))
  implicit val appConfig = new AppConfig(configuration, env, sc)
  
}
