package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.CandidateClient.CreateRequest
import play.api.libs.json.Json
import play.api.libs.ws.{ WS, WSResponse }
import play.api.Play.current

import scala.concurrent.Future

trait CandidateClient extends Client {
  def create(createRequest: CreateRequest): Future[WSResponse] = {
    post(getPostRequestUrl(), caseClassToTuples(createRequest))
  }
}

object CandidateClient extends CandidateClient {
  override val http = WS
  override val path = "candidates"

  case class CreateRequest(
    account_id: Option[Int],
    email: String,
    custom_candidate_id: Option[String],
    first_name: String,
    last_name: String
  ) {
    def isValid = true
  }

  object CreateRequest {
    implicit val createRequestFormat = Json.format[CreateRequest]
  }
}
