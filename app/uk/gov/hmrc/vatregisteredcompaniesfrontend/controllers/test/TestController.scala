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

package uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers.test

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class TestController @Inject()(
  connector: TestConnector,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def triggerDataImport(seed: String): Action[AnyContent] = Action.async { implicit request =>
    connector.trigger("trigger-mdg-data-post", seed) flatMap (_ =>
      Future.successful(Ok("data import successful")))
  }

  def triggerDataUpdate: Action[AnyContent] = Action.async { implicit request =>
    connector.trigger("trigger-mdg-data-update") flatMap (_ =>
      Future.successful(Ok("data update successful")))
  }

  def timeInfo : Action[AnyContent] = Action {
    import java.time._

    import sys.process._
    val logger = play.api.Logger("MACHINE_TIME_INFO")
    logger.info(s"""ZonedDateTime.now.withZoneSameInstant(ZoneId.of("Europe/London")).format(format.DateTimeFormatter.ISO_ZONED_DATE_TIME) is: ${ZonedDateTime.now.withZoneSameInstant(ZoneId.of("Europe/London")).format(format.DateTimeFormatter.ISO_ZONED_DATE_TIME)}""")
    logger.info(s"ZonedDateTime.now.format(format.DateTimeFormatter.ISO_ZONED_DATE_TIME) is: ${ZonedDateTime.now.format(java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME)}")
    val osDate = "date".!!
    val osDateUtc = "date --utc".!!
    logger.info(s"osDate : ${osDate.trim}")
    logger.info(s"osDateUTC: ${osDateUtc.trim}")
    logger.info(s"System.currentTimeMillis is ${System.currentTimeMillis}")
    logger.info(s"java.time.Clock.systemDefaultZone is ${java.time.Clock.systemDefaultZone}")
    Ok(
      s"""|
          |   ZonedDateTime.now.withZoneSameInstant(ZoneId.of("Europe/London")).format(format.DateTimeFormatter.ISO_ZONED_DATE_TIME) is: ${ZonedDateTime.now.withZoneSameInstant(ZoneId.of("Europe/London")).format(format.DateTimeFormatter.ISO_ZONED_DATE_TIME)}
          |   ZonedDateTime.now.format(format.DateTimeFormatter.ISO_ZONED_DATE_TIME) is: ${ZonedDateTime.now.format(java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME)}
          |   osDate : ${osDate.trim}
          |   osDateUtc:   ${osDateUtc.trim}
          |   System.currentTimeMillis is ${System.currentTimeMillis}
          |   java.time.Clock.systemDefaultZone is ${java.time.Clock.systemDefaultZone}|
          |
      """.stripMargin)
  }
}
