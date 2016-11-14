package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed.ReviewSectionReviewers

case class ReviewSectionReviewersRequest(
  reviewer1: ReviewSectionReviewerRequest,
  reviewer2: Option[ReviewSectionReviewerRequest],
  reviewer3: Option[ReviewSectionReviewerRequest]
)

object ReviewSectionReviewersRequest {
  def fromExchange(callback: ReviewSectionReviewers): ReviewSectionReviewersRequest = ReviewSectionReviewersRequest(
    ReviewSectionReviewerRequest.fromExchange(callback.reviewer_1),
    ReviewSectionReviewerRequest.fromExchange(callback.reviewer_2),
    ReviewSectionReviewerRequest.fromExchange(callback.reviewer_3)
  )

  implicit val reviewSectionReviewersFormat = Json.format[ReviewSectionReviewersRequest]
}
