package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed

import org.joda.time.{ DateTime, LocalDate }
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.BaseCallback
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed.ReviewedCallback
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands.FaststreamImplicits._

case class ReviewedCallbackRequest(
  received: DateTime,
  candidateId: String,
  customCandidateId: String,
  interviewId: Int,
  customInterviewId: Option[String],
  customInviteId: String,
  status: String,
  deadline: LocalDate,
  reviews: ReviewSectionRequest)
  extends BaseCallback(candidateId, customCandidateId, interviewId, customInterviewId, customInviteId, status, deadline)

object ReviewedCallbackRequest {
  def fromExchange(callback: ReviewedCallback): ReviewedCallbackRequest = ReviewedCallbackRequest(
    DateTime.now(),
    callback.candidate_id,
    callback.custom_candidate_id,
    callback.interview_id,
    callback.custom_interview_id,
    callback.custom_invite_id,
    callback.status,
    callback.deadline,
    ReviewSectionRequest.fromExchange(callback.reviews)
  )
  implicit val reviewedCallbackFormat = Json.format[ReviewedCallbackRequest]
}
