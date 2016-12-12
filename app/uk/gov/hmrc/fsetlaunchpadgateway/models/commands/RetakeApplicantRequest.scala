package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import org.joda.time.LocalDate
import play.api.libs.json.Json

case class RetakeApplicantRequest(interviewId: Int, candidateId: String, customInviteId: String, newDeadline: LocalDate, redirectUrl: String)

object RetakeApplicantRequest {
  implicit val retakeApplicantRequestFormat = Json.format[RetakeApplicantRequest]
}
