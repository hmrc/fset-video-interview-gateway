package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed.ReviewSectionReviewer

case class ReviewSectionReviewerRequest(name: String, email: String, comment: Option[String],
  question1: ReviewSectionQuestionRequest,
  question2: ReviewSectionQuestionRequest,
  question3: ReviewSectionQuestionRequest,
  question4: ReviewSectionQuestionRequest,
  question5: ReviewSectionQuestionRequest,
  question6: ReviewSectionQuestionRequest,
  question7: ReviewSectionQuestionRequest,
  question8: ReviewSectionQuestionRequest)

object ReviewSectionReviewerRequest {
  def fromExchange(callbackOpt: Option[ReviewSectionReviewer]): Option[ReviewSectionReviewerRequest] = callbackOpt.map { callback =>
    fromExchange(callback)
  }

  def fromExchange(callback: ReviewSectionReviewer): ReviewSectionReviewerRequest =
    ReviewSectionReviewerRequest(
      callback.name,
      callback.email,
      callback.comment,
      ReviewSectionQuestionRequest.fromExchange(callback.question_1),
      ReviewSectionQuestionRequest.fromExchange(callback.question_2),
      ReviewSectionQuestionRequest.fromExchange(callback.question_3),
      ReviewSectionQuestionRequest.fromExchange(callback.question_4),
      ReviewSectionQuestionRequest.fromExchange(callback.question_5),
      ReviewSectionQuestionRequest.fromExchange(callback.question_6),
      ReviewSectionQuestionRequest.fromExchange(callback.question_7),
      ReviewSectionQuestionRequest.fromExchange(callback.question_8)
    )

  implicit val reviewSectionReviewerFormat = Json.format[ReviewSectionReviewerRequest]
}
