package uk.gov.hmrc.fsetlaunchpadgateway.config

import play.api.Play.{ configuration, current }
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig.LaunchpadApiConfig
import uk.gov.hmrc.play.config.ServicesConfig
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

trait FrontendAppConfig {
  val launchpadApiConfig: LaunchpadApiConfig
}

object FrontendAppConfig extends FrontendAppConfig with ServicesConfig {

  case class LaunchpadApiConfig(
    key: String,
    baseUrl: String,
    accountId: Int,
    callbackUrl: String,
    testInterviewId: Int,
    testCandidateId: String
  )

  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  override lazy val launchpadApiConfig = configuration.underlying.as[LaunchpadApiConfig]("microservice.services.launchpad.api")
}
