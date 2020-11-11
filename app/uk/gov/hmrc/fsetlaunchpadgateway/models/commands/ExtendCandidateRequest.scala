package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands.FaststreamImplicits._

case class ExtendCandidateRequest(interviewId: Int, candidateId: String, newDeadline: LocalDate)

object ExtendCandidateRequest {
  implicit val extendCandidateRequestFormat = Json.format[ExtendCandidateRequest]
}
