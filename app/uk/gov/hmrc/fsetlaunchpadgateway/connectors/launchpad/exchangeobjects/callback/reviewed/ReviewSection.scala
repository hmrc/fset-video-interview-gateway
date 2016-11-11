package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed

import play.api.libs.json.Json

case class ReviewSection(
  total_average: ReviewSectionTotalAverage,
  reviewers: ReviewSectionReviewers
)

object ReviewSection {
  implicit val reviewSectionFormat = Json.format[ReviewSection]
}
