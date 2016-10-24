package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.QuestionCallback

case class QuestionCallbackRequest(candidateId: String, customCandidateId: String, interviewId: Int,
                                   customInterviewId: Option[String], customInviteId: String, status: String, deadline: LocalDate,
                                   questionNumber: String)
  extends BaseCallbackRequest(candidateId, customCandidateId, interviewId, customInterviewId, customInviteId, status, deadline)

object QuestionCallbackRequest {

  def apply(callback: QuestionCallback): QuestionCallbackRequest = QuestionCallbackRequest(
    callback.candidate_id,
    callback.custom_candidate_id,
    callback.interview_id,
    callback.custom_interview_id,
    callback.custom_invite_id,
    callback.status,
    callback.deadline,
    callback.question_number
  )

  implicit val questionCallbackFormat = Json.format[QuestionCallbackRequest]
}