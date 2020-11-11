package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import javax.inject.{ Inject, Named, Singleton }
import play.api.http.Status._
import play.api.libs.json.Format
import uk.gov.hmrc.fsetlaunchpadgateway.config.{ FrontendAppConfig, WSHttp }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.CandidateClient.{ CreateException, ExtendDeadlineException }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate.{ CreateRequest, CreateResponse, ExtendDeadlineRequest }

import scala.concurrent.ExecutionContext

import scala.concurrent.Future

object CandidateClient {
  case class CreateException(message: String, stringsToRemove: List[String])
    extends SanitizedClientException(message, stringsToRemove)

  case class ExtendDeadlineException(message: String, stringsToRemove: List[String])
    extends SanitizedClientException(message, stringsToRemove)
}

@Singleton
class CandidateClient @Inject() (
  @Named("httpExternal") val http: WSHttp,
  val config: FrontendAppConfig)(implicit override val ec: ExecutionContext)
  extends Client(http, "candidates", config) {
  def create(createRequest: CreateRequest)(implicit format: Format[CreateResponse]): Future[CreateResponse] =
    postWithResponseAsOrThrow[CreateResponse, CreateException](
      createRequest, getPostRequestUrl(), CreateException
    )

  def extendDeadline(candidateId: String, extendRequest: ExtendDeadlineRequest): Future[Unit] =
    post(getPostRequestUrl(s"/$candidateId/extend_deadline"), caseClassToTuples(extendRequest)).map {
      response =>
        if (response.status != OK) {
          throw ExtendDeadlineException(s"Error when extending deadline, response body was: ${response.body}", extendRequest.getSensitiveStrings)
        }
    }
}
