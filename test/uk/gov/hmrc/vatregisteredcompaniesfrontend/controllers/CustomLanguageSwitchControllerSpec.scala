/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.mvc.Result
import play.api.test.Helpers.*
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.utils.BaseSpec

import scala.concurrent.Future

class CustomLanguageSwitchControllerSpec extends BaseSpec {

  private val mockAppConfig = app.injector.instanceOf[AppConfig]
  private val mockLanguageUtils = app.injector.instanceOf[LanguageUtils]

  private val controller = new CustomLanguageSwitchController(
    configuration = app.configuration,
    appConfig = mockAppConfig,
    languageUtils = mockLanguageUtils,
    controllerComponents = mcc
  )

  "CustomLanguageSwitchController" should {

    "redirect to the fallback URL when an unsupported language is selected" in {
      val result: Future[Result] = controller.switchToLanguage("unsupported")(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(controller.fallbackURL)
    }

    "redirect to the fallback URL for supported language selection" in {
      val supportedLangKey = mockAppConfig.languageMap.keys.headOption.getOrElse("english")
      val expectedLangCode = mockAppConfig.languageMap(supportedLangKey).code
      val result: Future[Result] = controller.switchToLanguage(supportedLangKey)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(controller.fallbackURL)
      cookies(result).get("PLAY_LANG").map(_.value) shouldBe Some(expectedLangCode)
    }


    "handle unsupported language gracefully" in {
      val result: Future[Result] = controller.switchToLanguage("xyz")(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(controller.fallbackURL)
    }
  }
}
