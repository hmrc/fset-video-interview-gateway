package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects

import org.joda.time.{ DateTime, LocalDate }
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.SetupProcessCallback

case class SetupProcessCallbackRequest(received: DateTime, candidateId: String, customCandidateId: String, interviewId: Int,
  customInterviewId: Option[String], customInviteId: String, status: String, deadline: LocalDate)
  extends BaseCallbackRequest(received, candidateId, customCandidateId, interviewId, customInterviewId, customInviteId, status, deadline)

object SetupProcessCallbackRequest {

  def fromExchange(callback: SetupProcessCallback): SetupProcessCallbackRequest = SetupProcessCallbackRequest(
    DateTime.now(),
    callback.candidate_id,
    callback.custom_candidate_id,
    callback.interview_id,
    callback.custom_interview_id,
    callback.custom_invite_id,
    callback.status,
    callback.deadline
  )

  implicit val setupProcessCallbackFormat = Json.format[SetupProcessCallbackRequest]
}
