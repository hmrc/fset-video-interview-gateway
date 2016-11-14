package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed

import play.api.libs.json.Json

case class ReviewSectionReviewers(
  reviewer_1: ReviewSectionReviewer,
  reviewer_2: Option[ReviewSectionReviewer],
  reviewer_3: Option[ReviewSectionReviewer]
)

object ReviewSectionReviewers {
  implicit val reviewSectionReviewersFormat = Json.format[ReviewSectionReviewers]
}
