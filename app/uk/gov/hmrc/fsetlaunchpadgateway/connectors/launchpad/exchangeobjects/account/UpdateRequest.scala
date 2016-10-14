package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.account

import play.api.libs.json.Json

case class UpdateRequest(
  callback_url: Option[String]
)

object UpdateRequest {
  implicit val updateRequestFormat = Json.format[UpdateRequest]
}
