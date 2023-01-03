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

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.application

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData

case class RetakeRequest(
  interview_id: Int,
  account_id: Option[Int],
  deadline: String,
  employer_email: String,
  send_email: Boolean = true,
  retake_message: Option[String] = None,
  retake_screening: Option[Boolean] = None
) extends ContainsSensitiveData {
  def isValid: Boolean = true
  override def getSensitiveStrings: List[String] = List(employer_email)
}

object RetakeRequest {
  implicit val retakeRequestFormat = Json.format[RetakeRequest]
}
