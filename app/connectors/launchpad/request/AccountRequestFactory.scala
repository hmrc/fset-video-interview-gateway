package connectors.launchpad.request

import play.api.libs.json._

object AccountRequestFactory extends AccountRequestFactory {

  val basePath = "accounts"

  case class ListRequest(accountId: Option[Int]) extends GetApiRequest(basePath, accountId)

  object ListRequest {
    implicit val listRequestFormat = Json.format[ListRequest]
  }

  case class CreateRequest(
    accountId: Option[Int],
    company_name: String,
    sms_company_name: Option[String],
    company_comment: Option[String],
    email: Option[String],
    email_employers: Option[Boolean],
    email_applicants: Option[Boolean],
    send_feedback_email: Option[Boolean],
    logo_url: String,
    banner_url: Option[String],
    callback_url: String,
    status_frequency: Option[String],
    json_callback: Option[Boolean],
    timezone: Option[String]
  ) extends PostApiRequest(basePath, accountId) {

    def isValid = {
      status_frequency.forall(List("all", "final").contains(_)) &&
        timezone.forall(List("London").contains(_))
    }
  }

  object CreateRequest {
    implicit val createRequestFormat = Json.format[CreateRequest]
  }

}

trait AccountRequestFactory {

}
