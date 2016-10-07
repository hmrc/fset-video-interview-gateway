package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import play.api.Logger
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc._
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.{ AccountClient, CandidateClient, InterviewClient }
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands.{ CreateCandidateRequest, InviteCandidateRequest }
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

object ApplicationController extends ApplicationController {
  override val accountClient = AccountClient
  override val candidateClient = CandidateClient
  override val interviewClient = InterviewClient
}

trait ApplicationController extends BaseController {

  import CandidateClient.CreateResponse._

  val accountClient: AccountClient
  val candidateClient: CandidateClient
  val interviewClient: InterviewClient

  val launchpadAccountId = Some(FrontendAppConfig.launchpadApiConfig.accountId)

  def createCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[CreateCandidateRequest] { cc =>
      candidateClient.create(
        CandidateClient.CreateRequest(
          launchpadAccountId,
          cc.email,
          Some(cc.customCandidateId),
          cc.firstName,
          cc.lastName
        )
      ).map(wrappedResponse => Ok(Json.toJson(wrappedResponse.response))).recover(recoverFromBadCall)
    }
  }

  def inviteCandidate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[InviteCandidateRequest] { ic =>
      interviewClient.seamlessLoginInvite(
        launchpadAccountId,
        ic.interviewId,
        InterviewClient.SeamlessLoginInviteRequest(
          account_id = launchpadAccountId,
          candidate_id = ic.candidateId,
          custom_invite_id = Some(ic.customInviteId),
          send_email = None,
          redirect_url = Some(ic.redirectUrl)
        )
      ).map(wrappedResponse => Ok(Json.toJson(wrappedResponse.response))).recover(recoverFromBadCall)
    }
  }

  private def recoverFromBadCall: PartialFunction[Throwable, Result] = {
    case e: Throwable => InternalServerError(s"Error communicating with launchpad: ${e.getMessage}. " +
      s"Stacktrace: ${e.getStackTrace}")
    case _ => InternalServerError
  }
}

// scalastyle:off
/*

  {"app":"fset-launchpad-gateway","hostname":"public-app-13-fset-launchpad-gateway","timestamp":"2016-08-22 15:23:44.331+00:00","message":"Received callback => AnyContentAsJson({\"candidate_id\":\"cnd_4bf18c2e67fbdfe122614c894403ad1a\",\"custom_candidate_id\":\"CUSTOM_CSR_ID_TO_KNOW_WHO_IS_WHO\",\"interview_id\":13755,\"custom_interview_id\":null,\"custom_invite_id\":\"CSR_CUSTOM_INVITE_REFERENCE\",\"status\":\"final\"})\n\nWith static key = foobar123123123123123123123123123","logger":"application","thread":"play-akka.actor.default-dispatcher-557","level":"INFO","application.home":"/app/fset-launchpad-gateway-0.3.0"}
 */
// scalastyle:on