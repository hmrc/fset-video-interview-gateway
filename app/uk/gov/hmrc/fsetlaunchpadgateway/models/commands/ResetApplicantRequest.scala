package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands.FaststreamImplicits._

case class ResetApplicantRequest(interviewId: Int, candidateId: String, newDeadline: LocalDate)

object ResetApplicantRequest {
  implicit val resetApplicantRequestFormat = Json.format[ResetApplicantRequest]
}
