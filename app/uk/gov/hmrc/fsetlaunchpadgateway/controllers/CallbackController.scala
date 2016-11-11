package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import play.api.Logger
import play.api.libs.json.JsResultException
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.mvc._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.FaststreamClient
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.FaststreamClient.CallbackException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed.ReviewedCallbackRequest
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed.ReviewedCallback

import scala.concurrent.Future
import scala.util.{ Failure, Random, Success, Try }

object CallbackController extends CallbackController(FaststreamClient)

class CallbackController(faststreamClient: FaststreamClient) extends FrontendController {

  // scalastyle:off cyclomatic.complexity method.length
  def present(): Action[AnyContent] = Action.async { implicit request =>
    Logger.warn("Received callback => " + request.body.asJson.getOrElse("No callback body detected!").toString + "\n")

    Logger.warn(s"*** Content-type: ${request.contentType}\n" +
      s"*** Headers: ${request.headers}\n" +
      s"*** Body: ${request.body}\n" +
      s"*** Query string: ${request.rawQueryString}\n")

    request.body.asJson.map { contentAsJson =>
      Logger.debug(s"Callback received with body: $contentAsJson")

      val status = (contentAsJson \ "status").asOpt[String].map(_.toLowerCase)

      val tryParse = Try(status.map(_.toLowerCase) match {
        case Some("setup_process") =>
          Logger.debug("setup_process callback received!")
          val parsed = contentAsJson.as[SetupProcessCallback]
          faststreamClient.setupProcessCallback(SetupProcessCallbackRequest.fromExchange(parsed)).map(_ => Ok("Received"))
        case Some("view_practice_question") =>
          Logger.debug("view_practice_question callback received!")
          val parsed = contentAsJson.as[ViewPracticeQuestionCallback]
          faststreamClient.viewPracticeQuestionCallback(ViewPracticeQuestionCallbackRequest.fromExchange(parsed)).map(_ => Ok("Received"))
        case Some("question") =>
          Logger.debug("question callback received!")
          val parsed = contentAsJson.as[QuestionCallback]
          Logger.debug("Question parsed => " + parsed)
          faststreamClient.questionCallback(QuestionCallbackRequest.fromExchange(parsed)).map(_ => Ok("Received"))
        case Some("final") =>
          val parsed = contentAsJson.as[FinalCallback]
          Logger.debug("final callback received!")
          faststreamClient.finalCallback(FinalCallbackRequest.fromExchange(parsed)).map(_ => Ok("Received"))
        case Some("finished") =>
          val parsed = contentAsJson.as[FinishedCallback]
          Logger.debug("finished callback received!")
          faststreamClient.finishedCallback(FinishedCallbackRequest.fromExchange(parsed)).map(_ => Ok("Received"))
        case Some("reviewed") =>
          val parsed = contentAsJson.as[ReviewedCallback]
          Logger.debug("reviewed callback received!")
          faststreamClient.reviewedCallback(ReviewedCallbackRequest.fromExchange(parsed)).map(_ => Ok("Received"))
        case _ =>
          Logger.warn(s"Unknown callback type received! Status was $status, JSON body was $contentAsJson")
          Future.successful(BadRequest("Status was not recognised"))
      })

      tryParse match {
        case Success(result) =>
          result.recover {
            case ex =>
              Logger.warn(s"Error upstream.", ex)
              InternalServerError(s"Error upstream.")
          }
        case Failure(ex: JsResultException) =>
          Logger.warn(s"Could not parse payload with valid status. Raw request: ${request.body}. Exception: $ex")
          Future.successful(BadRequest("Request was malformed"))
        case Failure(ex: CallbackException) =>
          Logger.warn(s"Error when passing callback to faststream. Raw request: ${request.body}. Exception: $ex")
          Future.successful(InternalServerError("Request to upstream server failed"))
        case Failure(ex) =>
          Logger.warn(s"Could not parse payload. Raw request: ${request.body}")
          Future.successful(BadRequest("The request failed"))
      }
    }.getOrElse {
      Logger.warn(s"Callback received with invalid JSON or empty body received. Raw request: ${request.body}")
      Future.successful(BadRequest("Callback body was empty"))
    }
  }
  // scalastyle:on
}

// scalastyle:off
/*
  Example callback output, for reference:
  {timestamp":"2016-08-22 15:23:44.331+00:00","message":"Received callback => AnyContentAsJson({\"candidate_id\":\"cnd_4bf18c2e67fbdfe122614c894403ad1a\",\"custom_candidate_id\":\"CUSTOM_CSR_ID_TO_KNOW_WHO_IS_WHO\",\"interview_id\":13755,\"custom_interview_id\":null,\"custom_invite_id\":\"CSR_CUSTOM_INVITE_REFERENCE\",\"status\":\"final\"})\n\n","logger":"application","thread":"play-akka.actor.default-dispatcher-557","level":"INFO",}
 */

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

// scalastyle:on