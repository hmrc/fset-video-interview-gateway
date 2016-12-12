package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import play.api.libs.json.Format
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.ApplicationClient.{ ResetException, RetakeException }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.application._

import scala.concurrent.Future

object ApplicationClient extends ApplicationClient {
  override val path = "candidates"

  case class ResetException(message: String, stringsToRemove: List[String])
    extends SanitizedClientException(message, stringsToRemove)

  case class RetakeException(message: String, stringsToRemove: List[String])
    extends SanitizedClientException(message, stringsToRemove)

}

trait ApplicationClient extends Client {
  def reset(resetRequest: ResetRequest)(implicit format: Format[ResetResponse]): Future[ResetResponse] =
    postWithResponseAsOrThrow[ResetResponse, ResetException](
      resetRequest, getPostRequestUrl(), ResetException
    )

  def retake(retakeRequest: RetakeRequest)(implicit format: Format[RetakeResponse]): Future[RetakeResponse] =
    postWithResponseAsOrThrow[RetakeResponse, RetakeException](
      retakeRequest, getPostRequestUrl(), RetakeException
    )
}
