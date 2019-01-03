package uk.gov.hmrc.vatregisteredcompaniesfrontend.connectors

import javax.inject.Inject
import play.api.{Configuration, Environment}
import play.api.Mode.Mode
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.vatregisteredcompaniesfrontend.models.Lookup

class VatRegisteredCompaniesConnector @Inject()(
  http: HttpClient,
  environment: Environment,
  configuration: Configuration
) extends ServicesConfig {


  lazy val url: String = baseUrl("vat-registered-companies")

  override protected def mode: Mode = environment.mode
  override protected def runModeConfiguration: Configuration = configuration

  def lookup(lookup: Lookup) = ???


}
