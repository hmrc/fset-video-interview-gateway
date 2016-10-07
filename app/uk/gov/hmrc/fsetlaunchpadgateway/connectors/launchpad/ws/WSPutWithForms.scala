package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.ws

import play.api.http.HttpVerbs._
import play.api.libs.json.{ Json, Writes }
import play.api.http.HttpVerbs.{ PUT => PUT_VERB }
import uk.gov.hmrc.play.http.{ HeaderCarrier, HttpPut, HttpReads, HttpResponse }
import uk.gov.hmrc.play.http.ws.{ WSHttpResponse, WSPut, WSRequest }
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait WSPutWithForms extends WSPut {
  def doFormPut(url: String, body: Map[String, Seq[String]])(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    buildRequest(url).put(body).map(new WSHttpResponse(_))
  }

  // scalastyle:off
  def PUTForm[O](url: String, body: Map[String, Seq[String]])(implicit rds: HttpReads[O], hc: HeaderCarrier): Future[O] = {
    withTracing(PUT_VERB, url) {
      val httpResponse = doFormPut(url, body)
      executeHooks(url, PUT_VERB, Option(body), httpResponse)
      mapErrors(PUT_VERB, url, httpResponse).map(rds.read(PUT_VERB, url, _))
    }
  }
  // scalastyle:on
}
