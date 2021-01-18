/*
 * Copyright 2021 HM Revenue & Customs
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
