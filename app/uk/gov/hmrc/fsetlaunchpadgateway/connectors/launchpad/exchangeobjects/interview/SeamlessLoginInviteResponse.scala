package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData

case class SeamlessLoginInviteResponse(custom_invite_id: String, candidate_id: String, custom_candidate_id: String,
  link: SeamlessLoginLink, deadline: String) extends ContainsSensitiveData {
  override def getSensitiveStrings: List[String] = Nil
}

object SeamlessLoginInviteResponse {
  implicit val seamlessLoginInviteInnerResponse = Json.format[SeamlessLoginInviteResponse]
}
