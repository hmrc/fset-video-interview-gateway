package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects

import org.joda.time.{ DateTime, LocalDate }
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.FinalCallback

case class FinalCallbackRequest(received: DateTime, candidateId: String, customCandidateId: String, interviewId: Int,
  customInterviewId: Option[String], customInviteId: String, status: String, deadline: LocalDate)
  extends BaseCallbackRequest(received, candidateId, customCandidateId, interviewId, customInterviewId, customInviteId, status, deadline) {
}

object FinalCallbackRequest {
  def fromExchange(callback: FinalCallback): FinalCallbackRequest = FinalCallbackRequest(
    DateTime.now(),
    callback.candidate_id,
    callback.custom_candidate_id,
    callback.interview_id,
    callback.custom_interview_id,
    callback.custom_invite_id,
    callback.status,
    callback.deadline
  )

  implicit val finalCallbackRequestFormat = Json.format[FinalCallbackRequest]
}
