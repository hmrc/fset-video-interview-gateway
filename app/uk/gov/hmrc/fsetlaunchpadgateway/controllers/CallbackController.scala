package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import play.api.Logger
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.mvc._
import uk.gov.hmrc.fsetlaunchpadgateway.config.WSHttp
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig.faststreamApiConfig

import scala.concurrent.Future
import scala.util.Random

object CallbackController extends CallbackController

trait CallbackController extends FrontendController {
  def present(): Action[AnyContent] = Action.async { implicit request =>
    Logger.info("Received callback => " + request.body.asText.getOrElse("No callback body detected!") + "\n")

    Logger.debug(s"*** Content-type: ${request.contentType}" +
      s"*** Headers: ${request.headers}" +
      s"*** Body: ${request.body}" +
      s"*** Query string: ${request.rawQueryString}")

    // 1 in 10 calls will be a 500, just to test the retry
    if (Random.shuffle(1 to 10).head == 10) {
      Logger.info("Returned a random error on purpose")
      Future.successful(InternalServerError("A purposeful error to test retries occurred!!!![]*$:-()foo.exception\n\n\n "))
    } else {
      Logger.info("Returned a success message")
      Future.successful(Ok("Received"))
    }
  }

  // TODO: Remove this after successful production deploy/test
  def commsTest(): Action[AnyContent] = Action.async { implicit request =>
    WSHttp.GET(s"${faststreamApiConfig.url.host}/ping/ping").map { response =>
      Logger.debug(s"Response from faststream status = ${response.status}, body = ${response.body}")
      Ok
    }.recover {
      case ex => Logger.debug(s"Exception: " + ex); Ok
    }
  }
}

// scalastyle:off
/*
  Example callback output, for reference:
  {"app":"fset-launchpad-gateway","hostname":"public-app-13-fset-launchpad-gateway","timestamp":"2016-08-22 15:23:44.331+00:00","message":"Received callback => AnyContentAsJson({\"candidate_id\":\"cnd_4bf18c2e67fbdfe122614c894403ad1a\",\"custom_candidate_id\":\"CUSTOM_CSR_ID_TO_KNOW_WHO_IS_WHO\",\"interview_id\":13755,\"custom_interview_id\":null,\"custom_invite_id\":\"CSR_CUSTOM_INVITE_REFERENCE\",\"status\":\"final\"})\n\nWith static key = foobar123123123123123123123123123","logger":"application","thread":"play-akka.actor.default-dispatcher-557","level":"INFO","application.home":"/app/fset-launchpad-gateway-0.3.0"}
 */
// scalastyle:on