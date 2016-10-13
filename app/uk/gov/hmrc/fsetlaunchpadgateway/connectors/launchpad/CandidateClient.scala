package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.CandidateClient.CreateException
import play.api.libs.json.Format
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate.{ CreateRequest, CreateResponse }

import scala.concurrent.Future

object CandidateClient extends CandidateClient {
  override val path = "candidates"

  case class CreateException(message: String, stringsToRemove: List[String])
    extends SanitizedClientException(message, stringsToRemove)
}

trait CandidateClient extends Client {
  def create(createRequest: CreateRequest)(implicit format: Format[CreateResponse]): Future[CreateResponse] =
    postWithResponseAsOrThrow[CreateResponse, CreateException](
      createRequest, getPostRequestUrl(), CreateException
    )
}
