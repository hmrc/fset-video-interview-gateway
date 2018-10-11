package uk.gov.hmrc.fsetlaunchpadgateway.config

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import play.api.mvc.{ EssentialFilter, Request }
import play.api.{ Mode => _, _ }
import play.twirl.api.Html
import uk.gov.hmrc.crypto.ApplicationCrypto
import uk.gov.hmrc.play.config.{ AppName, ControllerConfig }
import uk.gov.hmrc.play.frontend.bootstrap.DefaultFrontendGlobal
import uk.gov.hmrc.play.frontend.filters._

abstract class FrontendGlobal
  extends DefaultFrontendGlobal {

  override val auditConnector = FrontendAuditConnector
  override val loggingFilter = LoggingFilter
  override val frontendAuditFilter = AuditFilter

  // Remove CSRF filters (specify override without them)
  override lazy val defaultFrontendFilters: Seq[EssentialFilter] = Seq(
    metricsFilter,
    HeadersFilter,
    SessionCookieCryptoFilter,
    deviceIdFilter,
    loggingFilter,
    frontendAuditFilter,
    CacheControlFilter.fromConfig("caching.allowedContentTypes"),
    RecoveryFilter
  )

  override def onStart(app: Application) {
    super.onStart(app)
    ApplicationCrypto.verifyConfiguration()
  }

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit rh: Request[_]): Html =
    Html("An error occurred")

  override def microserviceMetricsConfig(implicit app: Application): Option[Configuration] = app.configuration.getConfig("microservice.metrics")
}

object ControllerConfiguration extends ControllerConfig {
  lazy val controllerConfigs = Play.current.configuration.underlying.as[Config]("controllers")
}

object LoggingFilter extends FrontendLoggingFilter with MicroserviceFilterSupport {
  override def controllerNeedsLogging(controllerName: String): Boolean =
    ControllerConfiguration.paramsForController(controllerName).needsLogging
}

object AuditFilter extends FrontendAuditFilter with AppName with MicroserviceFilterSupport {

  override lazy val maskedFormFields = Seq("password")

  override lazy val applicationPort = None

  override lazy val auditConnector = FrontendAuditConnector

  override def controllerNeedsAuditing(controllerName: String): Boolean =
    ControllerConfiguration.paramsForController(controllerName).needsAuditing
}

object DevelopmentFrontendGlobal extends FrontendGlobal {
  override def onStart(app: Application): Unit = {
    Logger.warn("WHITE-LISTING DISABLED: Loading Development Frontend Global")
    super.onStart(app)
  }
}

object ProductionFrontendGlobal extends FrontendGlobal {
  override def filters: Seq[EssentialFilter] = WhitelistFilter +: super.filters
}

object TestFrontendGlobal extends FrontendGlobal {
  override def filters: Seq[EssentialFilter] = WhitelistFilter :: Nil
}
