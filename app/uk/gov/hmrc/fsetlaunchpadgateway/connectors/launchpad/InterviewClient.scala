package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.InterviewClient._
import play.api.http.Status._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.http.HttpResponse

object InterviewClient extends InterviewClient {
  override val path = "interviews"
  sealed case class InviteException(message: String, stringsToRemove: List[String]) extends SanitizedClientException(message, stringsToRemove)
}

trait InterviewClient extends Client {
  def list(accountId: Option[Int]): Future[HttpResponse] = {
    get(getGetRequestUrl(accountId))
  }

  def seamlessLoginInvite(accountId: Option[Int], interviewId: Int,
    seamlessLoginInviteRequest: SeamlessLoginInviteRequest): Future[SeamlessLoginInviteResponse] = {
    postWithResponseAsOrThrow[SeamlessLoginInviteResponse, InviteException](
      seamlessLoginInviteRequest,
      getPostRequestUrl(s"/${interviewId.toString}/seamless_login_invite"),
      InviteException
    )
  }

  // TODO: Remove this utility method before launch
  def create(createRequest: CreateRequest): Future[HttpResponse] = {
    post(getPostRequestUrl(), caseClassToTuples(createRequest))
  }

  // TODO: Remove this utility method before launch
  def update(interviewId: Int, updateRequest: UpdateRequest): Future[HttpResponse] = {
    put(getPostRequestUrl(s"/$interviewId"), caseClassToTuples(updateRequest))
  }
}
