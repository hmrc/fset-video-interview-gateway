package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate

import play.api.libs.json.Json

case class CreateRequest(
  account_id: Option[Int],
  email: String,
  custom_candidate_id: Option[String],
  first_name: String,
  last_name: String
) {
  def isValid: Boolean = true
}

object CreateRequest {
  implicit val createRequestFormat = Json.format[CreateRequest]
}
