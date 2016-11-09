package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview

import play.api.libs.json.Json

case class UpdateRequest(
  account_id: Option[Int],
  redirect_button_name: Option[String],
  show_redirect_button: Option[Boolean]
) {
}

object UpdateRequest {
  implicit val createRequestFormat = Json.format[UpdateRequest]
}
