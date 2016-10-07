package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import play.api.libs.json.Json

case class InviteCandidateRequest(interviewId: Int, candidateId: String, customInviteId: String, redirectUrl: String)

object InviteCandidateRequest {
  implicit val inviteCandidateRequestFormat = Json.format[InviteCandidateRequest]
}

