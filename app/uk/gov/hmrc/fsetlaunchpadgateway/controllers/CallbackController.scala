package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import javax.inject.{ Inject, Singleton }
import play.api.Logger
import play.api.libs.json.JsResultException
import play.api.mvc._
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.FaststreamClient
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.FaststreamClient.CallbackException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed.ReviewedCallbackRequest
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.{ ApplicationClient, CandidateClient, InterviewClient }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed.ReviewedCallback

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

@Singleton
class CallbackController @Inject() (
  config: FrontendAppConfig,
  mcc: MessagesControllerComponents,
  faststreamClient: FaststreamClient
)(implicit val ec: ExecutionContext) extends FrontendController(mcc) {

  // scalastyle:off cyclomatic.complexity method.length
  def present(): Action[AnyContent] = Action.async { implicit request =>
    Logger.info("Received callback => " + request.body.asJson.getOrElse("No callback body detected!").toString + "\n")

    Logger.info(s"*** Content-type: ${request.contentType}\n" +
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
