package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed.ReviewSection

case class ReviewSectionRequest(
  totalAverage: ReviewSectionTotalAverageRequest,
  reviewers: ReviewSectionReviewersRequest
)

object ReviewSectionRequest {
  def fromExchange(callback: ReviewSection): ReviewSectionRequest = ReviewSectionRequest(
    ReviewSectionTotalAverageRequest.fromExchange(callback.total_average),
    ReviewSectionReviewersRequest.fromExchange(callback.reviewers)
  )
  implicit val reviewSectionFormat = Json.format[ReviewSectionRequest]
}
