package uk.gov.hmrc.vatregisteredcompaniesfrontend.controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._

import scala.concurrent.Future
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.vatregisteredcompaniesfrontend.config.AppConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.views

@Singleton
class HelloWorld @Inject()(val messagesApi: MessagesApi, implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {
  // foo
  val helloWorld = Action.async { implicit request =>
    Future.successful(Ok(views.html.hello_world()))
  }

}
