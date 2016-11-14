package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData

case class ExtendDeadlineRequest(
  account_id: Int,
  interview_id: Int,
  employer_email: String,
  deadline: String,
  send_email: Boolean
) extends ContainsSensitiveData {
  def isValid: Boolean = true
  override def getSensitiveStrings: List[String] = List(employer_email)
}

object ExtendDeadlineRequest {
  implicit val extendDeadlineRequestFormat = Json.format[ExtendDeadlineRequest]
}
