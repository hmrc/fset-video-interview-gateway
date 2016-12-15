package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.application

import play.api.libs.json.Json

case class ResetLink(url: String, status: String, message: String)

object ResetLink {
  implicit val resetLinkFormat = Json.format[ResetLink]
}

