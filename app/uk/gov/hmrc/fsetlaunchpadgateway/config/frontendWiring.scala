package uk.gov.hmrc.fsetlaunchpadgateway.config

import java.util.Base64

import akka.actor.ActorSystem
import com.typesafe.config.Config
import play.api.Mode.Mode
import play.api.{ Configuration, Play }
import play.api.Play.current
import play.api.libs.ws.WSProxyServer
import play.api.mvc.{ Call, RequestHeader, Result }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.ws.WSPutWithForms
import uk.gov.hmrc.http.{ HttpDelete, HttpGet, HttpPost, HttpPut }
import uk.gov.hmrc.play.audit.http.connector.{ AuditConnector => Auditing }
import uk.gov.hmrc.play.config.{ AppName, ServicesConfig }
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.http.ws._
import uk.gov.hmrc.play.microservice.config.LoadAuditingConfig
import uk.gov.hmrc.play.microservice.filters.MicroserviceFilterSupport
import uk.gov.hmrc.whitelist.AkamaiWhitelistFilter

import scala.concurrent.Future

object FrontendAuditConnector extends Auditing with AppName {
  override lazy val auditingConfig = LoadAuditingConfig(s"auditing")
  override def appNameConfiguration: Configuration = Play.current.configuration
}

object WSHttpExternal extends WSHttp with WSProxy {
  override val hooks = NoneRequired
  override def wsProxyServer: Option[WSProxyServer] = WSProxyConfiguration("proxy")
}

object WSHttp extends WSHttp {
  override val hooks = NoneRequired
}

trait WSHttp extends HttpGet with WSGet
  with WSPutWithForms with HttpPost with WSPost with HttpDelete with WSDelete with HttpPut with AppName {
  override def appNameConfiguration: Configuration = Play.current.configuration
  override val configuration: Option[Config] = Option(Play.current.configuration.underlying)
  override def actorSystem: ActorSystem = Play.current.actorSystem
}

object FrontendAuthConnector extends AuthConnector with ServicesConfig {
  val serviceUrl = baseUrl("auth")
  lazy val http = WSHttp
  override def mode: Mode = Play.current.mode
  override def runModeConfiguration: Configuration = Play.current.configuration
}

object WhitelistFilter extends AkamaiWhitelistFilter with MicroserviceFilterSupport {

  // Whitelist Configuration
  private def whitelistConfig(key: String): Seq[String] =
    Some(new String(Base64.getDecoder.decode(Play.configuration.getString(key).getOrElse("")), "UTF-8"))
      .map(_.split(",")).getOrElse(Array.empty).toSeq

  override def noHeaderAction(
    f: (RequestHeader) => Future[Result],
    rh: RequestHeader
  ): Future[Result] = { f(rh) }

  // List of IP addresses
  override def whitelist: Seq[String] = whitelistConfig("whitelist")

  // Es. /ping/ping,/admin/details
  override def excludedPaths: Seq[Call] = whitelistConfig("whitelistExcludedCalls").map {
    path => Call("GET", path)
  }

  override def destination: Call = Call("GET", "https://www.apply-civil-service-fast-stream.service.gov.uk/outage-fset-faststream/index.html")
}
