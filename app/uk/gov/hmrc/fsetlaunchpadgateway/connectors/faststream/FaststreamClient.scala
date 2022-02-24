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

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream

import play.api.http.Status.OK

import javax.inject.{ Inject, Named, Singleton }
import uk.gov.hmrc.fsetlaunchpadgateway.config.{ FrontendAppConfig, WSHttp }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.FaststreamClient.CallbackException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed.ReviewedCallbackRequest
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ ExecutionContext, Future }
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }

object FaststreamClient {
  case class CallbackException(message: String) extends Exception(message)
}

@Singleton
class FaststreamClient @Inject() (val config: FrontendAppConfig, @Named("httpNormal") val http: WSHttp)(implicit ec: ExecutionContext) {
  val url = config.faststreamApiConfig.url

  def setupProcessCallback(callback: SetupProcessCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT[SetupProcessCallbackRequest, HttpResponse](s"${url.host}${url.base}$launchPadPrefix/setupProcessCallback", callback).map(okOrThrow)
  }

  def viewPracticeQuestionCallback(callback: ViewPracticeQuestionCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT[ViewPracticeQuestionCallbackRequest, HttpResponse](
      s"${url.host}${url.base}$launchPadPrefix/viewPracticeQuestion", callback).map(okOrThrow)
  }

  def questionCallback(callback: QuestionCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT[QuestionCallbackRequest, HttpResponse](
      s"${url.host}${url.base}$launchPadPrefix/questionCallback", callback).map(okOrThrow)
  }

  def finalCallback(callback: FinalCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT[FinalCallbackRequest, HttpResponse](
      s"${url.host}${url.base}$launchPadPrefix/finalCallback", callback).map(okOrThrow)
  }

  def finishedCallback(callback: FinishedCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT[FinishedCallbackRequest, HttpResponse](
      s"${url.host}${url.base}$launchPadPrefix/finishedCallback", callback).map(okOrThrow)
  }

  def reviewedCallback(callback: ReviewedCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT[ReviewedCallbackRequest, HttpResponse](
      s"${url.host}${url.base}$launchPadPrefix/reviewedCallback", callback).map(okOrThrow)
  }

  private def okOrThrow(response: HttpResponse) = {
    if (response.status != OK) {
      throw CallbackException(s"Response was not OK when forwarding callback to Faststream. Response: {$response.body}")
    }
  }

  private def getLaunchpadPrefix(inviteId: String) = s"/launchpad/$inviteId"
}
