package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.mvc._
import scala.concurrent.Future

object HelloWorld extends HelloWorld

trait HelloWorld extends FrontendController {
  val present = Action.async { implicit request =>
    Future.successful(Ok("Hello World"))
  }
}
