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

package utils

import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.html.layouts.{govukLayout, govukTemplate}
import uk.gov.hmrc.govukfrontend.views._
import uk.gov.hmrc.hmrcfrontend.views.html.components.hmrcReportTechnicalIssue
import views.html.vatregisteredcompaniesfrontend.components.{BeforeContent, Button, GovBetaBanner, InputText, Scripts}
import views.html.{ErrorTemplate, Head, Layout}

trait BaseSpec {
  val govukTemplate = new govukTemplate(new GovukHeader, new GovukFooter, new GovukSkipLink)
  val govukPhaseBanner = new GovukPhaseBanner(new govukTag)
  val layout = new Layout(
    new govukLayout(govukTemplate, new GovukHeader, new GovukFooter, new GovukBackLink),
    new Head,
    new Scripts,
    new GovBetaBanner(govukPhaseBanner),
    govukPhaseBanner,
    new hmrcReportTechnicalIssue
  )

  val formHelper = new FormWithCSRF
  val errorTemplate = new ErrorTemplate(layout)
  val inputText = new InputText(new govukInput(new govukErrorMessage, new govukHint, new govukLabel))
  val govukErrorSummary = new govukErrorSummary
  val button = new Button(new GovukButton)
  val govukCheckboxes = new GovukCheckboxes(new govukErrorMessage, new govukFieldset, new govukHint, new govukLabel)
  val beforeContent = new BeforeContent(new GovBetaBanner(govukPhaseBanner))
  val govukPanel = new GovukPanel
}
