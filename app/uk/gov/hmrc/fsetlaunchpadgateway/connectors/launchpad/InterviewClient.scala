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

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import javax.inject.{ Inject, Named, Singleton }
import uk.gov.hmrc.fsetlaunchpadgateway.config.{ FrontendAppConfig, WSHttp }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.InterviewClient._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview._
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.{ ExecutionContext, Future }

object InterviewClient {
  sealed case class InviteException(message: String, stringsToRemove: List[String]) extends SanitizedClientException(message, stringsToRemove)
}

@Singleton
class InterviewClient @Inject() (
  @Named("httpExternal") val http: WSHttp,
  val config: FrontendAppConfig)(implicit override val ec: ExecutionContext)
  extends Client(http, "interviews", config) {

  def list(accountId: Option[Int]): Future[HttpResponse] = {
    get(getGetRequestUrl(accountId))
  }

  def seamlessLoginInvite(accountId: Option[Int], interviewId: Int,
    seamlessLoginInviteRequest: SeamlessLoginInviteRequest): Future[SeamlessLoginInviteResponse] = {
    postWithResponseAsOrThrow[SeamlessLoginInviteResponse, InviteException](
      seamlessLoginInviteRequest,
      getPostRequestUrl(s"/${interviewId.toString}/seamless_login_invite"),
      InviteException
    )
  }

  // TODO: Remove this utility method before launch
  def create(createRequest: CreateRequest): Future[HttpResponse] = {
    post(getPostRequestUrl(), caseClassToTuples(createRequest))
  }

  // TODO: Remove this utility method before launch
  def update(interviewId: Int, updateRequest: UpdateRequest): Future[HttpResponse] = {
    put(getPostRequestUrl(s"/$interviewId"), caseClassToTuples(updateRequest))
  }
}
