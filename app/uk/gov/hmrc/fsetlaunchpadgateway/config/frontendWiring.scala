package uk.gov.hmrc.fsetlaunchpadgateway.config

import java.util.Base64

import play.api.Play
import play.api.mvc.Results._
import play.api.mvc.{ Call, RequestHeader, Result }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.ws.WSPutWithForms
import uk.gov.hmrc.play.audit.http.config.LoadAuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{ AuditConnector => Auditing }
import uk.gov.hmrc.play.config.{ AppName, RunMode, ServicesConfig }
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.http.ws.{ WSDelete, WSGet, WSPost }
import uk.gov.hmrc.whitelist.AkamaiWhitelistFilter
import play.api.Play.current
import uk.gov.hmrc.play.filters.MicroserviceFilterSupport

import scala.concurrent.Future

object FrontendAuditConnector extends Auditing with AppName {
  override lazy val auditingConfig = LoadAuditingConfig(s"auditing")
}

object WSHttp extends WSHttp {
  override val hooks = NoneRequired
}

trait WSHttp extends WSGet with WSPutWithForms with WSPost with WSDelete with AppName with RunMode {
}

object FrontendAuthConnector extends AuthConnector with ServicesConfig {
  val serviceUrl = baseUrl("auth")
  lazy val http = WSHttp
}

object WhitelistFilter extends AkamaiWhitelistFilter with RunMode with MicroserviceFilterSupport {

  // Whitelist Configuration
  private def whitelistConfig(key: String): Seq[String] =
    Some(new String(Base64.getDecoder.decode(Play.configuration.getString(key).getOrElse("")), "UTF-8"))
      .map(_.split(",")).getOrElse(Array.empty).toSeq

  // List of IP addresses
  override def whitelist: Seq[String] = whitelistConfig("whitelist")

  // Es. /ping/ping,/admin/details
  override def excludedPaths: Seq[Call] = whitelistConfig("whitelistExcludedCalls").map {
    path => Call("GET", path)
  }

  override def destination: Call = Call("GET", "https://www.apply-civil-service-fast-stream.service.gov.uk/outage-fset-faststream/index.html")

}
