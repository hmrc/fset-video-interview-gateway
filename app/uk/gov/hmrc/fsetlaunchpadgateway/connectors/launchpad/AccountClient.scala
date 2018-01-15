package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.account.{ CreateRequest, UpdateRequest }

import scala.concurrent.Future
import uk.gov.hmrc.http.HttpResponse

object AccountClient extends AccountClient {
  override val path = "accounts"
}

// TODO: Remove this utility account client before launch
trait AccountClient extends Client {
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
    // https://www-qa.tax.service.gov.uk/fset-video-interview-gateway/callback
    put(s"$apiBaseUrl/$path/$accountId", caseClassToTuples(updateRequest))
  }

  def getOwnAccountDetails: Future[HttpResponse] = {
    get(s"$apiBaseUrl/$path/self")
  }
}
