package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview

import play.api.libs.json.Json

case class SeamlessLoginInviteRequest(
  account_id: Option[Int],
  candidate_id: String,
  custom_invite_id: Option[String],
  send_email: Option[Boolean] = None,
  redirect_url: Option[String]
)

object SeamlessLoginInviteRequest {
  implicit val seamlessLoginInviteRequestFormat = Json.format[SeamlessLoginInviteRequest]
}
