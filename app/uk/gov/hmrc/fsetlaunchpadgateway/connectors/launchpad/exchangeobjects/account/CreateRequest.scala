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

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.account

import play.api.libs.json.Json

case class CreateRequest(
  account_id: Option[Int],
  company_name: String,
  sms_company_name: Option[String],
  company_comment: Option[String],
  email: Option[String],
  email_employers: Option[Boolean],
  email_applicants: Option[Boolean],
  send_feedback_email: Option[Boolean],
  logo_url: String,
  banner_url: Option[String],
  callback_url: String,
  status_frequency: Option[String],
  json_callback: Option[Boolean],
  timezone: Option[String]
) {
  def isValid: Boolean = {
    status_frequency.forall(List("all", "final").contains(_)) &&
      timezone.forall(List("London").contains)
  }
}

object CreateRequest {
  implicit val createRequestFormat = Json.format[CreateRequest]
}
