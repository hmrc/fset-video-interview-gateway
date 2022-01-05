/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
