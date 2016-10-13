package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import play.api.Logger
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc._
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.{ AccountClient, CandidateClient, InterviewClient }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects._
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands._
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

object ApplicationController extends ApplicationController {
  override val accountClient = AccountClient
  override val candidateClient = CandidateClient
  override val interviewClient = InterviewClient
}

trait ApplicationController extends BaseController {

  val accountClient: AccountClient
  val candidateClient: CandidateClient
  val interviewClient: InterviewClient

  val launchpadAccountId = Some(FrontendAppConfig.launchpadApiConfig.accountId)

  def createCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[CreateCandidateRequest] { cc =>
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
        }.recover(recoverFromBadCall)
    }
  }

  def inviteCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[InviteCandidateRequest] { ic =>
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
        }.recover(recoverFromBadCall)
    }
  }

  private def recoverFromBadCall: PartialFunction[Throwable, Result] = {
    case e: Throwable =>
      Logger.warn(s"Error communicating with launchpad: ${e.getMessage}. Stacktrace: ${e.getStackTrace}")
      InternalServerError("Error communicating with Launchpad")
  }
}
