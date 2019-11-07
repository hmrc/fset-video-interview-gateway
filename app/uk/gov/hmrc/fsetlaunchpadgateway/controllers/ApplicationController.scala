package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import play.api.Logger
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc._
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.{ AccountClient, ApplicationClient, CandidateClient, InterviewClient }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects._
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands._
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

object ApplicationController extends ApplicationController {
  override val accountClient = AccountClient
  override val candidateClient = CandidateClient
  override val interviewClient = InterviewClient
  override val applicationClient = ApplicationClient
}

trait ApplicationController extends BaseController {

  val accountClient: AccountClient
  val candidateClient: CandidateClient
  val interviewClient: InterviewClient
  val applicationClient: ApplicationClient

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

  val launchpadAccountId = Some(FrontendAppConfig.launchpadApiConfig.accountId)
  val employerEmail = FrontendAppConfig.launchpadApiConfig.extensionValidUserEmailAddress

  def createCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[CreateCandidateRequest] { cc =>
      val jsonBody = request.body
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
        }.recover(recoverFromBadCall(Operations.CreateCandidate, jsonBody))
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
    withJsonBody[ExtendCandidateRequest] { ec =>
      val jsonBody = request.body
      candidateClient.extendDeadline(
        ec.candidateId,
        candidate.ExtendDeadlineRequest(
          FrontendAppConfig.launchpadApiConfig.accountId,
          ec.interviewId,
          FrontendAppConfig.launchpadApiConfig.extensionValidUserEmailAddress,
          ec.newDeadline.toString("yyyy-MM-dd"),
          send_email = false
        )
      ).map { _ =>
          Ok
        }.recover(recoverFromBadCall(Operations.ExtendCandidate, jsonBody))
    }
  }

  private def recoverFromBadCall(operation: Operations.Operation, request: JsValue): PartialFunction[Throwable, Result] = {
    case e: Throwable =>
      Logger.warn(s"Error communicating with launchpad for operation:${operation.name}. Request:$request. " +
        s"Error:${e.getMessage}. Stacktrace:${e.getStackTrace}.")
      InternalServerError("Error communicating with Launchpad")
  }

  private def recoverFromBadResetOrRetakeCall(operation: Operations.Operation, request: JsValue): PartialFunction[Throwable, Result] = {
    case e: Throwable =>
      Logger.warn(s"Error communicating with launchpad for operation:${operation.name}. Request:$request. " +
        s"Error: ${e.getMessage}. Stacktrace: ${e.getStackTrace}")
      if (e.getMessage.contains("Interview ID and/or Candidate ID are invalid") ||
        e.getMessage.contains("Candidate is not applicable for application reset")) {
        Conflict("Video interview cannot be reset due to being in an unresetable state.")
      } else {
        InternalServerError("Error communicating with Launchpad")
      }
  }
}
