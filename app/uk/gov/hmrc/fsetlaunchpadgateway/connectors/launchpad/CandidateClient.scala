package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.CandidateClient.CreateException
import play.api.http.Status._
import play.api.libs.json.Format
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate.{ CreateRequest, CreateResponse }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

object CandidateClient extends CandidateClient {
  override val path = "candidates"

  case class CreateException(message: String, stringsToRemove: List[String])
    extends SanitizedClientException(message, stringsToRemove)

  abstract class SanitizedClientException(message: String, stringsToRemove: List[String])
    extends Exception(sanitizeLog(message, stringsToRemove))

  def sanitizeLog(stringToSanitize: String, stringsToRemove: List[String]): String = {
    ""
  }
}

trait CandidateClient extends Client {
  def create(createRequest: CreateRequest)(implicit format: Format[CreateResponse]): Future[CreateResponse] = {
    post(getPostRequestUrl(), caseClassToTuples(createRequest)).map { response =>
      if (response.status == OK) {
        Try(response.json.\\("response").head.as[CreateResponse]) match {
          case Success(resp) => resp
          case Failure(ex) => throw CreateException("Unexpected response from Cubiks", Nil)
        }
      } else {
        throw CreateException(s"Received a ${response.status} code when trying to create a candidate. " +
          s"Response: ${response.body}", Nil)
      }
    }
  }
}
