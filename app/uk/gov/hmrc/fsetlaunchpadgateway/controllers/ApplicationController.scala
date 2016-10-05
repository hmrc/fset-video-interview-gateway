package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import play.api.Logger
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.mvc._
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.{ AccountClient, CandidateClient }

import scala.concurrent.Future
import scala.util.Random

object ApplicationController extends ApplicationController
{
  override val accountClient = AccountClient
  override val candidateClient = CandidateClient
}

trait ApplicationController extends FrontendController {

  val accountClient: AccountClient
  val candidateClient: CandidateClient

  val launchpadAccountId = Some(FrontendAppConfig.launchpadApiConfig.accountId)

  def createCandidate() = Action.async(parse.json) { implicit request =>
    withJsonBody[CreateCandidateRequest] { cc =>
      CandidateClient.create(
        CandidateClient.CreateRequest(
          launchpadAccountId,
          cc.email,
          Some(cc.launchpadId),
          cc.firstName,
          cc.lastName
        )
      )
    }
  }
}

// scalastyle:off
/*

  {"app":"fset-launchpad-gateway","hostname":"public-app-13-fset-launchpad-gateway","timestamp":"2016-08-22 15:23:44.331+00:00","message":"Received callback => AnyContentAsJson({\"candidate_id\":\"cnd_4bf18c2e67fbdfe122614c894403ad1a\",\"custom_candidate_id\":\"CUSTOM_CSR_ID_TO_KNOW_WHO_IS_WHO\",\"interview_id\":13755,\"custom_interview_id\":null,\"custom_invite_id\":\"CSR_CUSTOM_INVITE_REFERENCE\",\"status\":\"final\"})\n\nWith static key = foobar123123123123123123123123123","logger":"application","thread":"play-akka.actor.default-dispatcher-557","level":"INFO","application.home":"/app/fset-launchpad-gateway-0.3.0"}
 */
// scalastyle:on