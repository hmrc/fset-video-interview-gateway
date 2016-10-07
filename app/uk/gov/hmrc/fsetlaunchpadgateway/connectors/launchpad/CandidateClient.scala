package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.CandidateClient.{ CreateException, CreateRequest, CreateResponse }
import play.api.libs.json.Json
import play.api.libs.ws.{ WS, WSResponse }
import play.api.Play.current
import play.api.http.Status._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object CandidateClient extends CandidateClient {
  override val path = "candidates"

  case class CreateRequest(
    account_id: Option[Int],
    email: String,
    custom_candidate_id: Option[String],
    first_name: String,
    last_name: String
  ) {
    def isValid: Boolean = true
  }

  object CreateRequest {
    implicit val createRequestFormat = Json.format[CreateRequest]
  }

  case class CreateInnerResponse(
    candidate_id: String,
    custom_candidate_id: String
  )

  object CreateInnerResponse {
    implicit val createInnerResponseFormat = Json.format[CreateInnerResponse]
  }

  case class CreateResponse(response: CreateInnerResponse)

  object CreateResponse {
    implicit val createResponseFormat = Json.format[CreateResponse]
  }

  sealed case class CreateException(message: String) extends Exception(message)
}

trait CandidateClient extends Client {
  def create(createRequest: CreateRequest): Future[CreateResponse] = {
    post(getPostRequestUrl(), caseClassToTuples(createRequest)).map { response =>
      if (response.status == OK) {
        response.json.as[CreateResponse]
      } else {
        throw CreateException(s"Received a ${response.status} code when trying to create a candidate. Response: ${response.body}")
      }
    }
  }
}
