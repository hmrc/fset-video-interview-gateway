package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.AccountClient.{ CreateRequest, UpdateRequest }
import play.api.libs.json.Json
import play.api.libs.ws.{ WS, WSResponse }
import play.api.Play.current
import uk.gov.hmrc.fsetlaunchpadgateway.WSHttp
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future

trait AccountClient extends Client {
  def list(accountId: Option[Int]): Future[HttpResponse] = {
    get(getGetRequestUrl(accountId))
  }

  def create(createRequest: CreateRequest): Future[HttpResponse] = {
    post(getPostRequestUrl(), caseClassToTuples(createRequest))
  }

  def getSpecific(accountId: Int): Future[HttpResponse] = {
    get(getGetRequestUrl(None) + "/" + accountId.toString)
  }

  def updateAccount(accountId: Int, updateRequest: UpdateRequest): Future[HttpResponse] = {
    // https://www-qa.tax.service.gov.uk/fset-launchpad-gateway/callback
    put(s"$apiBaseUrl/$path/$accountId", caseClassToTuples(updateRequest))
  }

  def getOwnAccountDetails: Future[HttpResponse] = {
    get(s"$apiBaseUrl/$path/self")
  }
}

object AccountClient extends AccountClient {
  override val path = "accounts"

  case class UpdateRequest(
    callback_url: Option[String]
  )

  object UpdateRequest {
    implicit val updateRequestFormat = Json.format[UpdateRequest]
  }

  case class CreateRequest(
    account_id: Option[Int],
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
    def isValid: Boolean = {
      status_frequency.forall(List("all", "final").contains(_)) &&
        timezone.forall(List("London").contains)
    }
  }

  object CreateRequest {
    implicit val createRequestFormat = Json.format[CreateRequest]
  }
}
