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

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.BaseCallback
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands.FaststreamImplicits._

case class ReviewedCallback(
  candidate_id: String,
  custom_candidate_id: String,
  interview_id: Int,
  custom_interview_id: Option[String],
  custom_invite_id: String,
  status: String,
  deadline: LocalDate,
  reviews: ReviewSection)
  extends BaseCallback(candidate_id, custom_candidate_id, interview_id, custom_interview_id, custom_invite_id, status, deadline)

object ReviewedCallback {
  implicit val reviewedCallbackFormat = Json.format[ReviewedCallback]
}
