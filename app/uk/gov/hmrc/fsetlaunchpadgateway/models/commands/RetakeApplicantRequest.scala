package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands.FaststreamImplicits._

case class RetakeApplicantRequest(interviewId: Int, candidateId: String, newDeadline: LocalDate)

object RetakeApplicantRequest {
  implicit val retakeApplicantRequestFormat = Json.format[RetakeApplicantRequest]
}
