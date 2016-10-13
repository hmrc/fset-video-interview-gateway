package uk.gov.hmrc.fsetlaunchpadgateway

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.ws.WSPutWithForms
import uk.gov.hmrc.play.audit.http.config.LoadAuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{ AuditConnector => Auditing }
import uk.gov.hmrc.play.config.{ AppName, RunMode, ServicesConfig }
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.http.ws.{ WSDelete, WSGet, WSPost, WSPut }

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
