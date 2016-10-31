package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import org.joda.time.LocalDate
import play.api.libs.json.Json

case class ExtendCandidateRequest(interviewId: Int, candidateId: String, newDeadline: LocalDate)

object ExtendCandidateRequest {
  implicit val extendCandidateRequestFormat = Json.format[ExtendCandidateRequest]
}
