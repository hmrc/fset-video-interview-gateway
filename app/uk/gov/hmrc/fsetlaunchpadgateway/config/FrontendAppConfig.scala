package uk.gov.hmrc.fsetlaunchpadgateway.config

import play.api.Play.{ configuration, current }
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig.{ FaststreamApiConfig, LaunchpadApiConfig }
import uk.gov.hmrc.play.config.ServicesConfig
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

trait FrontendAppConfig {
  val faststreamApiConfig: FaststreamApiConfig
  val launchpadApiConfig: LaunchpadApiConfig
}

object FrontendAppConfig extends FrontendAppConfig with ServicesConfig {

  case class LaunchpadApiConfig(
    key: String,
    baseUrl: String,
    accountId: Int,
    callbackUrl: String
  )

  case class FaststreamApiConfig(
    url: FaststreamUrl
  )

  case class FaststreamUrl(
    host: String,
    base: String
  )

  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  override lazy val faststreamApiConfig = configuration.underlying.as[FaststreamApiConfig]("microservice.services.faststream")
  override lazy val launchpadApiConfig = configuration.underlying.as[LaunchpadApiConfig]("microservice.services.launchpad.api")
}
