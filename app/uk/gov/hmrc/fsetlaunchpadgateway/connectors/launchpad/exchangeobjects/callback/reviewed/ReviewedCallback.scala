package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.BaseCallback

case class ReviewedCallback(
  candidate_id: String,
  custom_candidate_id: String,
  interview_id: Int,
  custom_interview_id: Option[String],
  custom_invite_id: String,
  status: String,
  deadline: LocalDate,
  reviews: ReviewSection)
  extends BaseCallback(candidate_id, custom_candidate_id, interview_id, custom_interview_id, custom_invite_id, status, deadline)

object ReviewedCallback {
  implicit val reviewedCallbackFormat = Json.format[ReviewedCallback]
}
