package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.account

import play.api.libs.json.Json

case class UpdateRequest(
  status_frequency: Option[String]
)

object UpdateRequest {
  implicit val updateRequestFormat = Json.format[UpdateRequest]
}
