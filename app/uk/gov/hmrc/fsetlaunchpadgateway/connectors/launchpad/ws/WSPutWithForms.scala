package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.ws

import play.api.http.HttpVerbs.{ PUT => PUT_VERB }
import uk.gov.hmrc.http.hooks.HookData
import uk.gov.hmrc.play.http.ws.{ WSHttpResponse, WSPut }

import scala.concurrent.ExecutionContext
import uk.gov.hmrc.http.{ HeaderCarrier, HttpPut, HttpReads, HttpResponse }

import scala.concurrent.Future

trait WSPutWithForms extends HttpPut with WSPut {
  def doFormPut(url: String, body: Map[String, Seq[String]])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    buildRequest(url).put(body).map(WSHttpResponse(_))
  }

  // scalastyle:off
  def PUTForm[O](url: String, body: Map[String, Seq[String]])(implicit rds: HttpReads[O], hc: HeaderCarrier, ec: ExecutionContext): Future[O] = {

    withTracing(PUT_VERB, url) {
      val httpResponse = doFormPut(url, body)
      executeHooks(url, PUT_VERB, Option(HookData.FromMap(body)), httpResponse)
      mapErrors(PUT_VERB, url, httpResponse).map(rds.read(PUT_VERB, url, _))
    }
  }
  // scalastyle:on
}
