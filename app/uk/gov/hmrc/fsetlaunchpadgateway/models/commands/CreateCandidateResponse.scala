package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate.CreateResponse

case class CreateCandidateResponse(
  candidateId: String,
  customCandidateIid: String
)

object CreateCandidateResponse {
  implicit val createCandidateResponseFormat = Json.format[CreateCandidateResponse]

  def fromResponse(response: CreateResponse): CreateCandidateResponse = {
    CreateCandidateResponse(response.candidate_id, response.custom_candidate_id)
  }
}
