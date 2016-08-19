package connectors.launchpad.request

import play.api.libs.json._

object InterviewRequestFactory extends InterviewRequestFactory {

  val basePath = "interviews"

  case class ListRequest(accountId: Option[Int]) extends GetApiRequest(basePath, accountId)
  object ListRequest { implicit val listRequestFormat = Json.format[ListRequest] }

}

trait InterviewRequestFactory {

}
