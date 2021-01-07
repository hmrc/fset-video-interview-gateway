package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import javax.inject.{ Inject, Named, Singleton }
import uk.gov.hmrc.fsetlaunchpadgateway.config.{ FrontendAppConfig, WSHttp }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.account.{ CreateRequest, UpdateRequest }
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.{ ExecutionContext, Future }

// TODO: Remove this utility account client before launch
@Singleton
class AccountClient @Inject() (
  @Named("httpExternal") val http: WSHttp,
  val config: FrontendAppConfig)(implicit override val ec: ExecutionContext)
  extends Client(http, "accounts", config) {
  def list(accountId: Option[Int]): Future[HttpResponse] = {
    get(getGetRequestUrl(accountId))
  }

  def create(createRequest: CreateRequest): Future[HttpResponse] = {
    post(getPostRequestUrl(), caseClassToTuples(createRequest))
  }

  def getSpecific(accountId: Int): Future[HttpResponse] = {
    get(getGetRequestUrl(None) + "/" + accountId.toString)
  }

  def updateAccount(accountId: Int, updateRequest: UpdateRequest): Future[HttpResponse] = {
    // https://www-qa..../fset-video-interview-gateway/callback
    put(s"$apiBaseUrl/$path/$accountId", caseClassToTuples(updateRequest))
  }

  def getOwnAccountDetails: Future[HttpResponse] = {
    get(s"$apiBaseUrl/$path/self")
  }
}
