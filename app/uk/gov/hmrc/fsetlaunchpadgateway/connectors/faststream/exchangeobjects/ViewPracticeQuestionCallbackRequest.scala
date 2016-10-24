package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.ViewPracticeQuestionCallback

case class ViewPracticeQuestionCallbackRequest(candidateId: String, customCandidateId: String, interviewId: Int,
                                               customInterviewId: Option[String], customInviteId: String, status: String, deadline: LocalDate)
  extends BaseCallbackRequest(candidateId, customCandidateId, interviewId,
    customInterviewId, customInviteId, status, deadline)

object ViewPracticeQuestionCallbackRequest {

  def apply(callback: ViewPracticeQuestionCallback): ViewPracticeQuestionCallbackRequest = ViewPracticeQuestionCallbackRequest(
    callback.candidate_id,
    callback.custom_candidate_id,
    callback.interview_id,
    callback.custom_interview_id,
    callback.custom_invite_id,
    callback.status,
    callback.deadline
  )

  implicit val viewPracticeQuestionCallbackFormat = Json.format[ViewPracticeQuestionCallbackRequest]
}
