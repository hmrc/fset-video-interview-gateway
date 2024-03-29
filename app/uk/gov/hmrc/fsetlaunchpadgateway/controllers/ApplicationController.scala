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

package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import javax.inject.{ Inject, Singleton }
import play.api.Logging
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc._
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.{ ApplicationClient, CandidateClient, InterviewClient }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects._
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class ApplicationController @Inject() (
  config: FrontendAppConfig,
  cc: ControllerComponents,
  candidateClient: CandidateClient,
  interviewClient: InterviewClient,
  applicationClient: ApplicationClient
)(implicit val ec: ExecutionContext) extends BackendController(cc) with Logging {

  object Operations {
    sealed abstract class Operation {
      def name: String
    }

    case object CreateCandidate extends Operation {
      override def name: String = "createCandidate"
    }

    case object InviteCandidate extends Operation {
      override def name: String = "inviteCandidate"
    }

    case object ResetCandidate extends Operation {
      override def name: String = "resetCandidate"
    }

    case object RetakeCandidate extends Operation {
      override def name: String = "retakeCandidate"
    }

    case object ExtendCandidate extends Operation {
      override def name: String = "extendCandidate"
    }
  }

  val launchpadAccountId: Option[Int] = Some(config.launchpadApiConfig.accountId)
  val employerEmail: String = config.launchpadApiConfig.extensionValidUserEmailAddress

  def createCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[CreateCandidateRequest] { cc =>
      val obfuscatedJsonBody = Json.toJson(CreateCandidateRequest(
        email = "HIDDEN", customCandidateId = cc.customCandidateId, firstName = "HIDDEN", lastName = "HIDDEN"
      ))
      candidateClient.create(
        candidate.CreateRequest(
          launchpadAccountId,
          cc.email,
          Some(cc.customCandidateId),
          cc.firstName,
          cc.lastName
        )
      ).map { createResponse =>
          Ok(Json.toJson(CreateCandidateResponse.fromResponse(createResponse)))
        }.recover(recoverFromBadCall(Operations.CreateCandidate, obfuscatedJsonBody))
    }
  }

  def inviteCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[InviteCandidateRequest] { ic =>
      val jsonBody = request.body
      interviewClient.seamlessLoginInvite(
        launchpadAccountId,
        ic.interviewId,
        interview.SeamlessLoginInviteRequest(
          account_id = launchpadAccountId,
          candidate_id = ic.candidateId,
          custom_invite_id = Some(ic.customInviteId),
          send_email = None,
          redirect_url = Some(ic.redirectUrl)
        )
      ).map { seamlessLoginInviteResponse =>
          Ok(Json.toJson(InviteCandidateResponse.fromResponse(seamlessLoginInviteResponse)))
        }.recover(recoverFromBadCall(Operations.InviteCandidate, jsonBody))
    }
  }

  def resetCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[ResetApplicantRequest] { resetRequest =>
      val jsonBody = request.body
      applicationClient.reset(
        application.ResetRequest(
          interview_id = resetRequest.interviewId,
          account_id = launchpadAccountId,
          deadline = resetRequest.newDeadline.toString("yyyy-MM-dd"),
          employer_email = employerEmail,
          send_email = false
        ),
        resetRequest.candidateId
      ).map { resetResponse =>
          Ok(Json.toJson(ResetApplicantResponse.fromResponse(resetResponse)))
        }.recover(recoverFromBadResetOrRetakeCall(Operations.ResetCandidate, jsonBody))
    }
  }

  def retakeCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[RetakeApplicantRequest] { retakeRequest =>
      {
        val jsonBody = request.body
        applicationClient.retake(
          application.RetakeRequest(
            interview_id = retakeRequest.interviewId,
            account_id = launchpadAccountId,
            deadline = retakeRequest.newDeadline.toString("yyyy-MM-dd"),
            employer_email = employerEmail,
            send_email = false
          ),
          retakeRequest.candidateId
        ).map { retakeResponse => Ok(Json.toJson(RetakeApplicantResponse.fromResponse(retakeResponse)))
          }.recover(recoverFromBadResetOrRetakeCall(Operations.RetakeCandidate, jsonBody))
      }
    }
  }

  def extendCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[ExtendCandidateRequest] { ecReq =>
      val jsonBody = request.body
      candidateClient.extendDeadline(
        ecReq.candidateId,
        candidate.ExtendDeadlineRequest(
          config.launchpadApiConfig.accountId,
          ecReq.interviewId,
          config.launchpadApiConfig.extensionValidUserEmailAddress,
          ecReq.newDeadline.toString("yyyy-MM-dd"),
          send_email = false
        )
      ).map { _ => Ok }.recover(recoverFromBadCall(Operations.ExtendCandidate, jsonBody))
    }
  }

  private def recoverFromBadCall(operation: Operations.Operation, request: JsValue): PartialFunction[Throwable, Result] = {
    case e: Throwable =>
      logger.warn(s"Error communicating with launchpad for operation ${operation.name}. Request: $request. " +
        s"Error: ${e.getMessage}. Stacktrace: ${e.getStackTrace}.")
      InternalServerError("Error communicating with Launchpad")
  }

  private def recoverFromBadResetOrRetakeCall(operation: Operations.Operation, request: JsValue): PartialFunction[Throwable, Result] = {
    case e: Throwable =>
      logger.warn(s"Error communicating with launchpad for operation ${operation.name}. Request: $request. " +
        s"Error: ${e.getMessage}. Stacktrace: ${e.getStackTrace}")
      if (e.getMessage.contains("Interview ID and/or Candidate ID are invalid") ||
        e.getMessage.contains("Candidate is not applicable for application reset")) {
        Conflict("Video interview cannot be reset due to not being in a state that can be reset.")
      } else {
        InternalServerError("Error communicating with Launchpad")
      }
  }
}
