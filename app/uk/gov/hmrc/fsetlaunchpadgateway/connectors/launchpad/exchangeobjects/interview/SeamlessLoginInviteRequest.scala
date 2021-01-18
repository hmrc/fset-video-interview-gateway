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

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData

case class SeamlessLoginInviteRequest(
  account_id: Option[Int],
  candidate_id: String,
  custom_invite_id: Option[String],
  send_email: Option[Boolean] = None,
  redirect_url: Option[String]
) extends ContainsSensitiveData {
  override def getSensitiveStrings: List[String] = Nil
}

object SeamlessLoginInviteRequest {
  implicit val seamlessLoginInviteRequestFormat = Json.format[SeamlessLoginInviteRequest]
}
