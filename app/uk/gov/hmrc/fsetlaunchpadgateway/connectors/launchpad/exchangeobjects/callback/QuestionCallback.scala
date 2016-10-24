package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback

import org.joda.time.LocalDate
import play.api.libs.json.Json

case class QuestionCallback(candidate_id: String, custom_candidate_id: String, interview_id: Int,
  custom_interview_id: Option[String], custom_invite_id: String, status: String, deadline: LocalDate,
  question_number: String)
  extends BaseCallback(candidate_id, custom_candidate_id, interview_id, custom_interview_id, custom_invite_id, status, deadline)

object QuestionCallback {
  implicit val questionCallbackFormat = Json.format[QuestionCallback]
}
