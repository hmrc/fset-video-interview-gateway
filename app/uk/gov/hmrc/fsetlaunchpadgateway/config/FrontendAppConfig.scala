package uk.gov.hmrc.fsetlaunchpadgateway.config

import java.util.Base64

import javax.inject.{ Inject, Singleton }
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.{ Configuration, Environment }

@Singleton
class FrontendAppConfig @Inject() (
  val config: Configuration, val environment: Environment) {

  case class LaunchpadApiConfig(
    extensionValidUserEmailAddress: String,
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

  private def loadConfig(key: String) = config.getOptional[String](key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  lazy val faststreamApiConfig = config.underlying.as[FaststreamApiConfig]("microservice.services.faststream")
  lazy val launchpadApiConfig = config.underlying.as[LaunchpadApiConfig]("microservice.services.launchpad.api")

  // Whitelist Configuration
  private def whitelistConfig(key: String): Seq[String] = Some(
    new String(Base64.getDecoder().decode(config.getOptional[String](key).getOrElse("")), "UTF-8")
  ).map(_.split(",")).getOrElse(Array.empty).toSeq

  lazy val whitelist = whitelistConfig("whitelist")
  lazy val whitelistExcluded = whitelistConfig("whitelistExcludedCalls")
}
