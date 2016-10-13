package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData

case class CreateRequest(
  account_id: Option[Int],
  email: String,
  custom_candidate_id: Option[String],
  first_name: String,
  last_name: String
) extends ContainsSensitiveData {
  def isValid: Boolean = true
  override def getSensitiveStrings: List[String] = List(email, first_name, last_name)
}

object CreateRequest {
  implicit val createRequestFormat = Json.format[CreateRequest]
}
