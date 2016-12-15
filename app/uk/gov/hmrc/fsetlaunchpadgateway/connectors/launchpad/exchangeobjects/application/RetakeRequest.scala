package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.application

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData

case class RetakeRequest(
  interview_id: Int,
  account_id: Option[Int],
  deadline: String,
  employer_email: String,
  send_email: Boolean = true,
  retake_message: Option[String] = None,
  retake_screening: Option[Boolean] = None
) extends ContainsSensitiveData {
  def isValid: Boolean = true
  override def getSensitiveStrings: List[String] = List(employer_email)
}

object RetakeRequest {
  implicit val retakeRequestFormat = Json.format[RetakeRequest]
}
