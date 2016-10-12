package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.InterviewClient._
import play.api.http.Status._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview._
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object InterviewClient extends InterviewClient {
  override val path = "interviews"
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
          response.json.\\("response").head.as[SeamlessLoginInviteResponse]
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
