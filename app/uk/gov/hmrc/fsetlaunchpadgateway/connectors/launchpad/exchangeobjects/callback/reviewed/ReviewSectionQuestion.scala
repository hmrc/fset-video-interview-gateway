package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed

import play.api.libs.json.Json

case class ReviewSectionQuestion(text: String, id: Int, review_criteria_1: ReviewSectionCriteria,
  review_criteria_2: ReviewSectionCriteria)

object ReviewSectionQuestion {
  implicit val reviewSectionReviewerQuestion = Json.format[ReviewSectionQuestion]
}
