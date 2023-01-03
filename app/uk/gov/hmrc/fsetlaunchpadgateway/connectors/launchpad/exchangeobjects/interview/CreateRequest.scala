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

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview

import play.api.libs.json.Json

case class CreateRequest(
  account_id: Option[Int],
  title: String,
  comments: Option[String],
  custom_interview_id: Option[String],
  responsibilities: String, // Job responsibilities
  qualifications: Option[String],
  rerecord: Option[Boolean],
  deadline: Option[String],
  introduction_message: Option[String],
  closing_message: Option[String],
  time_limit: Option[Int],
  redirect_url: String,
  redirect_button_name: Option[String],
  show_redirect_button: Option[Boolean],
  default_language: Option[String],
  questions: List[Question]

) {
  def isValid: Boolean = {
    deadline.forall(theDeadline =>
      // Candidate specific deadline
      theDeadline.matches("\\d+") ||
        // Hard date deadline
        theDeadline.matches("\\d{4}-\\d{2}-\\d{2}")
    ) &&
      time_limit.forall(List(15, 30, 45, 60).contains) &&
      default_language.forall(List("en").contains)
  }
}

object CreateRequest {
  implicit val createRequestFormat = Json.format[CreateRequest]
}
