package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.InterviewClient._
import play.api.libs.json.Json
import play.api.libs.ws.{ WS, WSResponse }
import play.api.Play.current
import play.api.http.Status._
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object InterviewClient extends InterviewClient {
  override val path = "interviews"

  case class SeamlessLoginInviteRequest(
    account_id: Option[Int],
    candidate_id: String,
    custom_invite_id: Option[String],
    send_email: Option[Boolean] = None,
    redirect_url: Option[String]
  )

  object SeamlessLoginInviteRequest {
    implicit val seamlessLoginInviteRequestFormat = Json.format[SeamlessLoginInviteRequest]
  }

  case class Question(
    text: String,
    limit: Option[Int],
    preparation_time: Option[Int]
  )

  object Question {
    implicit val questionFormat = Json.format[Question]
  }

  case class CreateRequest(
    account_id: Option[Int],
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
    questions: List[Question]

  ) {
    def isValid: Boolean = {
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

  case class SeamlessLoginLink(url: String, status: String, message: String)

  object SeamlessLoginLink {
    implicit val seamlessLoginLinkFormat = Json.format[SeamlessLoginLink]
  }

  case class SeamlessLoginInviteInnerResponse(custom_invite_id: String, candidate_id: String, custom_candidate_id: String,
    link: SeamlessLoginLink, deadline: String)

  object SeamlessLoginInviteInnerResponse {
    implicit val seamlessLoginInviteInnerResponse = Json.format[SeamlessLoginInviteInnerResponse]
  }

  // scalastyle:off
  /*
  {"interview_id":13916,"custom_interview_id":null,"custom_invite_id":"CSR_CUSTOM_INVITE_REFERENCE_HENRI_1","title":"API Test Interview","candidate_id":"cnd_a5c3aa07985df493681dbca8b2b9c13d","custom_candidate_id":"CUSTOM_CANDIDATE_HENRI_1","email":"a@b.com","link":{"url":"https://test.launchpadrecruitsapp.com/seamless_invite/cadf6e9d","status":"success","message":""},"deadline":""}}
   */
  // scalastyle:on
  case class SeamlessLoginInviteResponse(response: SeamlessLoginInviteInnerResponse)

  object SeamlessLoginInviteResponse {
    implicit val seamlessLoginInviteResponseFormat = Json.format[SeamlessLoginInviteResponse]
  }

  sealed case class InviteException(message: String) extends Exception(message)
}

trait InterviewClient extends Client {
  def list(accountId: Option[Int]): Future[HttpResponse] = {
    get(getGetRequestUrl(accountId))
  }

  def seamlessLoginInvite(accountId: Option[Int], interviewId: Int,
    seamlessLoginInviteRequest: SeamlessLoginInviteRequest): Future[SeamlessLoginInviteResponse] = {
    post(
      getPostRequestUrl(s"/${interviewId.toString}/seamless_login_invite"),
      caseClassToTuples(seamlessLoginInviteRequest)).map { response =>
        if (response.status == OK) {
          response.json.as[SeamlessLoginInviteResponse]
        } else {
          throw InviteException(s"Received a ${response.status} code when trying to seamless login invite a candidate. " +
            s"Response: ${response.body}")
        }
      }
  }

  def create(createRequest: CreateRequest): Future[HttpResponse] = {
    post(getPostRequestUrl(), caseClassToTuples(createRequest))
  }
}
