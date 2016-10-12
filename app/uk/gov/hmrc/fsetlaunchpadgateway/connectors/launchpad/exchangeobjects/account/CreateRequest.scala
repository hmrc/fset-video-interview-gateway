package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.account

import play.api.libs.json.Json

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
