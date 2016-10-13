package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData

case class CreateResponse(
  candidate_id: String,
  custom_candidate_id: String
) extends ContainsSensitiveData {
  override def getSensitiveStrings: List[String] = Nil
}

object CreateResponse {
  implicit val createResponseFormat = Json.format[CreateResponse]
}
