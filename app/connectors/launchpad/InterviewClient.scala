package connectors.launchpad

import connectors.launchpad.InterviewClient.CreateRequest
import play.api.libs.json.Json
import play.api.libs.ws.{ WS, WSResponse }
import play.api.Play.current

import scala.concurrent.Future

trait InterviewClient extends Client {
  def list(accountId: Option[Int]): Future[WSResponse] = {
    get(getGetRequestUrl(accountId))
  }

  // ?questions[][text]=QUESTION+1&questions[][limit]=30&questions[][text]=QUESTION+2&questions[][limit]=60
  def create(createRequest: CreateRequest): Future[WSResponse] = {
    post(getPostRequestUrl, caseClassToTuples(createRequest))
  }
}

object InterviewClient extends InterviewClient {
  override val http = WS
  override val path = "interviews"

  case class Question(
    text: String,
    limit: Option[Int],
    preparation_time: Option[Int]
  )

  object Question {
    implicit val questionFormat = Json.format[Question]
  }

  case class CreateRequest(
    accountId: Option[Int],
    title: String,
    comments: Option[String],
    custom_interview_id: Option[String],
    responsibilities: String, // Job responsibilities
    qualifications: Option[String],
    rerecord: Option[Boolean],
    deadline: Option[String],
    introduction_message: Option[String],
    closing_message: Option[String],
    time_limit: Option[Int],
    redirect_url: String,
    redirect_button_name: Option[String],
    show_redirect_button: Option[Boolean],
    default_language: Option[String],
    questions: List[Question] // TODO: Serialise these question

  ) {
    def isValid = {
      deadline.forall(theDeadline =>
        // Candidate specific deadline
        theDeadline.matches("\\d+") ||
          // Hard date deadline
          theDeadline.matches("\\d{4}-\\d{2}-\\d{2}")
      ) &&
        time_limit.forall(List(15, 30, 45, 60).contains) &&
        default_language.forall(List("en").contains)
    }
  }

  object CreateRequest {
    implicit val createRequestFormat = Json.format[CreateRequest]
  }
}
