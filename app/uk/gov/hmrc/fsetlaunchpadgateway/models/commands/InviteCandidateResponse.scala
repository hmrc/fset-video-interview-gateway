package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview.{ SeamlessLoginInviteResponse, SeamlessLoginLink }

case class InviteCandidateResponse(
  custom_invite_id: String, candidate_id: String, custom_candidate_id: String,
  testUrl: String, deadline: String
)

object InviteCandidateResponse {
  implicit val inviteCandidateResponseFormat = Json.format[InviteCandidateResponse]

  def fromResponse(response: SeamlessLoginInviteResponse): InviteCandidateResponse = {
    InviteCandidateResponse(response.custom_invite_id, response.candidate_id,
      response.custom_candidate_id, response.link.url, response.deadline)
  }
}
