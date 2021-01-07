package uk.gov.hmrc.fsetlaunchpadgateway.config

import akka.actor.ActorSystem
import com.typesafe.config.Config
import javax.inject.{ Inject, Singleton }
import play.api.Application
import play.api.libs.ws.{ WSClient, WSProxyServer }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.ws.WSPutWithForms
import uk.gov.hmrc.http.{ HttpDelete, HttpGet, HttpPost, HttpPut }
import uk.gov.hmrc.play.http.ws._

@Singleton
class WSHttpExternal @Inject() (
  val wsClient: WSClient,
  val application: Application) extends WSHttp with WSProxy {
  override def wsProxyServer: Option[WSProxyServer] = WSProxyConfiguration("proxy", application.configuration)
}

@Singleton
class WSHttpNormal @Inject() (
  val wsClient: WSClient,
  val application: Application) extends WSHttp {
}

trait WSHttp extends HttpGet with WSGet
  with WSPutWithForms with HttpPost with WSPost with HttpDelete with WSDelete with HttpPut {
  val application: Application
  override val hooks = NoneRequired
  override lazy val configuration: Option[Config] = Option(application.configuration.underlying)
  override lazy val actorSystem: ActorSystem = application.actorSystem
}
