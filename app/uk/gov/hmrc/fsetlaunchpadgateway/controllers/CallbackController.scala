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
    Logger.info("Received callback => " + request.body.asJson.getOrElse("No callback body detected!").toString + "\n")

    Logger.debug(s"*** Content-type: ${request.contentType}" +
      s"*** Headers: ${request.headers}" +
      s"*** Body: ${request.body}" +
      s"*** Query string: ${request.rawQueryString}")

    // 1 in 10 calls will be a 500, just to test the retry
    if (true) {
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
      Logger.warn(s"Response from faststream status = ${response.status}, body = ${response.body}")
      Ok
    }.recover {
      case ex => Logger.warn(s"Exception: " + ex); Ok
    }
  }
}

// scalastyle:off
/*
  Example callback output, for reference:
  {timestamp":"2016-08-22 15:23:44.331+00:00","message":"Received callback => AnyContentAsJson({\"candidate_id\":\"cnd_4bf18c2e67fbdfe122614c894403ad1a\",\"custom_candidate_id\":\"CUSTOM_CSR_ID_TO_KNOW_WHO_IS_WHO\",\"interview_id\":13755,\"custom_interview_id\":null,\"custom_invite_id\":\"CSR_CUSTOM_INVITE_REFERENCE\",\"status\":\"final\"})\n\n","logger":"application","thread":"play-akka.actor.default-dispatcher-557","level":"INFO",}
 */
// scalastyle:on

/*
{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"finished","deadline":"2016-10-28"}

{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"final","deadline":"2016-10-28"}

{"candidate_id":"cnd_9b0b1fc7fd81329e7109d217108c6452","custom_candidate_id":"FSCND-6bc83ac4-e547-41f4-8825-5661d5d85a56","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2f2ed237-f0a1-4c04-bc53-536dae436425","status":"question","deadline":"2016-10-28","question_number":"3"}

{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"question","deadline":"2016-10-28","question_number":"4"}

{"candidate_id":"cnd_9b0b1fc7fd81329e7109d217108c6452","custom_candidate_id":"FSCND-6bc83ac4-e547-41f4-8825-5661d5d85a56","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2f2ed237-f0a1-4c04-bc53-536dae436425","status":"question","deadline":"2016-10-28","question_number":"2"}

{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"question","deadline":"2016-10-28","question_number":"3"}

{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"question","deadline":"2016-10-28","question_number":"2"}

{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"question","deadline":"2016-10-28","question_number":"1"}

{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"view_practice_question","deadline":"2016-10-28"}

{"candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6","custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9","interview_id":13917,"custom_interview_id":null,"custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19","status":"setup_process","deadline":"2016-10-28"}
 */