package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate

import play.api.libs.json.Json

case class CreateResponse(
  candidate_id: String,
  custom_candidate_id: String
)

object CreateResponse {
  implicit val createResponseFormat = Json.format[CreateResponse]
}
