package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview

import play.api.libs.json.Json

case class SeamlessLoginLink(url: String, status: String, message: String)

object SeamlessLoginLink {
  implicit val seamlessLoginLinkFormat = Json.format[SeamlessLoginLink]
}

