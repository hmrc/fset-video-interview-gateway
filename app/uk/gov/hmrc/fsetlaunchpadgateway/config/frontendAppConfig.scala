package uk.gov.hmrc.fsetlaunchpadgateway

import play.api.Play.{ configuration, current }
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig {
  val launchpadApiKey: String
  val launchpadApiBaseUrl: String
  val launchpadApiAccountId: Int
}

object FrontendAppConfig extends AppConfig with ServicesConfig {

  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  override lazy val launchpadApiKey = config("launchpad").getString("api.key").get
  override lazy val launchpadApiBaseUrl = config("launchpad").getString("api.baseUrl").get
  override lazy val launchpadApiAccountId = config("launchpad").getInt("api.accountId").get
}
