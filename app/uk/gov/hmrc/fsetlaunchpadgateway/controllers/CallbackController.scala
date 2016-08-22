package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import play.api.Logger
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.mvc._

import scala.concurrent.Future
import scala.util.Random

object CallbackController extends CallbackController

trait CallbackController extends FrontendController {
  def present(staticKey: String) = Action.async { implicit request =>
    Logger.info("Received callback => " + request.body + "\n\nWith static key = " + staticKey)

    // 1 in 10 calls will be a 500, just to test the retry
    if (Random.shuffle(1 to 10).head == 10) {
      Logger.info("Returned a random error on purpose")
      Future.successful(InternalServerError("A purposeful error to test retries occurred!!!![]*$:-()foo.exception\n\n\n "))
    } else {
      Logger.info("Returned a success message")
      Future.successful(Ok("Received"))
    }
  }
}
