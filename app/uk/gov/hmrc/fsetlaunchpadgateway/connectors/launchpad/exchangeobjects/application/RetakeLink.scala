package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.application

import play.api.libs.json.Json

case class RetakeLink(url: String, status: String, message: String)

object RetakeLink {
  implicit val retakeLinkFormat = Json.format[RetakeLink]
}

