package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback

import org.joda.time.LocalDate
import play.api.libs.json.Json

case class QuestionCallback(candidate_id: String, custom_candidate_id: String, interview_id: Int,
  custom_interview_id: String, custom_invite_id: String, status: String, deadline: LocalDate,
  question_number: String) extends BaseCallback(candidate_id, custom_candidate_id, interview_id,
  custom_interview_id, custom_invite_id, status, deadline)

object QuestionCallback {
  implicit val questionCallbackFormat = Json.format[QuestionCallback]
}

// scalastyle:off
/*

{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,
"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"question","deadline":"2016-10-28","question_number":"2"}

{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"question","deadline":"2016-10-28","question_number":"1"}

 */
// scalastyle:on
