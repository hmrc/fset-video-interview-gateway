package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed.ReviewSectionQuestion

case class ReviewSectionQuestionRequest(id: Int, reviewCriteria1: ReviewSectionCriteriaRequest,
  reviewCriteria2: ReviewSectionCriteriaRequest)

object ReviewSectionQuestionRequest {
  def fromExchange(callback: ReviewSectionQuestion): ReviewSectionQuestionRequest = ReviewSectionQuestionRequest(
    callback.id,
    ReviewSectionCriteriaRequest.fromExchange(callback.review_criteria_1),
    ReviewSectionCriteriaRequest.fromExchange(callback.review_criteria_2)
  )
  implicit val reviewSectionReviewerQuestion = Json.format[ReviewSectionQuestionRequest]
}
