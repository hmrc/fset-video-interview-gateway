package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.application

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands.{ InviteCandidateResponse, ResetApplicantResponse }

case class ResetResponse(
  interview_id: String,
  custom_interview_id: String,
  custom_invite_id: String,
  title: String,
  email: String,
  link: String,
  deadline: String
) extends ContainsSensitiveData {
  override def getSensitiveStrings: List[String] = List(email)
}

object ResetResponse {
  implicit val resetResponseFormat = Json.format[ResetResponse]
}
