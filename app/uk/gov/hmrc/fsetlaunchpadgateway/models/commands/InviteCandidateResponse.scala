/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview.SeamlessLoginInviteResponse

case class InviteCandidateResponse(
  customInviteId: String, candidateId: String, customCandidateId: String,
  testUrl: String, deadline: String
)

object InviteCandidateResponse {
  implicit val inviteCandidateResponseFormat = Json.format[InviteCandidateResponse]

  def fromResponse(response: SeamlessLoginInviteResponse): InviteCandidateResponse = {
    InviteCandidateResponse(response.custom_invite_id, response.candidate_id,
      response.custom_candidate_id, response.link.url, response.deadline)
  }
}
