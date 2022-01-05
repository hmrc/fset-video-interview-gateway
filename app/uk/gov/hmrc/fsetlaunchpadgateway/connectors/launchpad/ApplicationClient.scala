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

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import javax.inject.{ Inject, Named, Singleton }
import play.api.libs.json.Format
import uk.gov.hmrc.fsetlaunchpadgateway.config.{ FrontendAppConfig, WSHttp }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.ApplicationClient.{ ResetException, RetakeException }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.application._

import scala.concurrent.{ ExecutionContext, Future }

object ApplicationClient {
  case class ResetException(message: String, stringsToRemove: List[String])
    extends SanitizedClientException(message, stringsToRemove)

  case class RetakeException(message: String, stringsToRemove: List[String])
    extends SanitizedClientException(message, stringsToRemove)
}

@Singleton
class ApplicationClient @Inject() (
  @Named("httpExternal") val http: WSHttp,
  val config: FrontendAppConfig)(implicit override val ec: ExecutionContext)
  extends Client(http, "candidates", config) {

  def reset(resetRequest: ResetRequest, candidateId: String)(implicit format: Format[ResetResponse]): Future[ResetResponse] =
    postWithResponseAsOrThrow[ResetResponse, ResetException](
      resetRequest,
      getPostRequestUrl(s"/$candidateId/reset"),
      ResetException
    )

  def retake(retakeRequest: RetakeRequest, candidateId: String)(implicit format: Format[RetakeResponse]): Future[RetakeResponse] = {
    postWithResponseAsOrThrow[RetakeResponse, RetakeException](
      retakeRequest,
      getPostRequestUrl(s"/$candidateId/retake"),
      RetakeException
    )
  }
}
