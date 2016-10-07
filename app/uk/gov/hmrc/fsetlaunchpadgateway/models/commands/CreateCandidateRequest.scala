package uk.gov.hmrc.fsetlaunchpadgateway.models.commands

import play.api.libs.json.Json

case class CreateCandidateRequest(email: String, customCandidateId: String, firstName: String, lastName: String)

object CreateCandidateRequest {
  implicit val createCandidateRequestFormat = Json.format[CreateCandidateRequest]
}
