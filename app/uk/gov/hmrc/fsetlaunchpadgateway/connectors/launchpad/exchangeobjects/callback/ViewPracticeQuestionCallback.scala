package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands.FaststreamImplicits._

case class ViewPracticeQuestionCallback(candidate_id: String, custom_candidate_id: String, interview_id: Int,
  custom_interview_id: Option[String], custom_invite_id: String, status: String, deadline: LocalDate)
  extends BaseCallback(candidate_id, custom_candidate_id, interview_id,
    custom_interview_id, custom_invite_id, status, deadline)

object ViewPracticeQuestionCallback {
  implicit val viewPracticeQuestionCallbackFormat = Json.format[ViewPracticeQuestionCallback]
}
