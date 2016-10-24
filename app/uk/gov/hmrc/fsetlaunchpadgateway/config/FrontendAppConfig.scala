package uk.gov.hmrc.fsetlaunchpadgateway.config

import java.util.Base64

import play.api.Play.{ configuration, current }
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig.{ FaststreamApiConfig, LaunchpadApiConfig }
import uk.gov.hmrc.play.config.ServicesConfig
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.Play

trait FrontendAppConfig {
  val faststreamApiConfig: FaststreamApiConfig
  val launchpadApiConfig: LaunchpadApiConfig
  val whitelist: Seq[String]
  val whitelistExcluded: Seq[String]
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

  lazy val faststreamApiConfig = configuration.underlying.as[FaststreamApiConfig]("microservice.services.faststream")
  lazy val launchpadApiConfig = configuration.underlying.as[LaunchpadApiConfig]("microservice.services.launchpad.api")

  // Whitelist Configuration
  private def whitelistConfig(key: String): Seq[String] = Some(
    new String(Base64.getDecoder().decode(Play.configuration.getString(key).getOrElse("")), "UTF-8")
  ).map(_.split(",")).getOrElse(Array.empty).toSeq

  lazy val whitelist = whitelistConfig("whitelist")
  lazy val whitelistExcluded = whitelistConfig("whitelistExcludedCalls")
}
