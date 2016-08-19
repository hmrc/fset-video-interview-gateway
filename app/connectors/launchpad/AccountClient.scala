package connectors.launchpad

import play.api.libs.json.Json
import play.api.libs.ws.{ WS, WSResponse }

import play.api.Play.current
import scala.concurrent.Future

object AccountClient extends AccountClient {
  override val http = WS
  override val path = "accounts"
}

trait AccountClient extends Client {
  def list(accountId: Option[Int]): Future[WSResponse] = {
    http.url(getRequestUrl(accountId)).withHeaders(getAuthHeaders: _*).get()
  }
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
) {
  def isValid = {
    status_frequency.forall(List("all", "final").contains(_)) &&
      timezone.forall(List("London").contains(_))
  }
}

object CreateRequest {
  implicit val createRequestFormat = Json.format[CreateRequest]
}
