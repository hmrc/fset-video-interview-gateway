package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed

import play.api.libs.json.Json

case class ReviewSectionReviewer(name: String, email: String, comment: Option[String],
  question_1: ReviewSectionQuestion,
  question_2: ReviewSectionQuestion,
  question_3: ReviewSectionQuestion,
  question_4: ReviewSectionQuestion,
  question_5: ReviewSectionQuestion,
  question_6: ReviewSectionQuestion,
  question_7: ReviewSectionQuestion,
  question_8: ReviewSectionQuestion)

object ReviewSectionReviewer {
  implicit val reviewSectionReviewerFormat = Json.format[ReviewSectionReviewer]
}
