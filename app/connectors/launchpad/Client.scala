package connectors.launchpad

import connectors.launchpad.request.{ ApiRequest, GetApiRequest, PostApiRequest }
import java.nio.charset.StandardCharsets
import java.util.Base64

import play.api.Logger
import play.api.mvc.Request
import uk.gov.hmrc.fsetlaunchpadgateway.{ FrontendAppConfig, WSHttp }
import uk.gov.hmrc.fsetlaunchpadgateway.controllers.HelloWorld
import uk.gov.hmrc.play.http.{ HeaderCarrier, HttpResponse }
import play.api.http.Status._
import play.api.libs.ws.{ WS, WSResponse }
import play.api.Play.current

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import language.postfixOps

object Client extends Client {
  override val http = WS
}

trait Client {

  val http: WS.type

  // scalastyle:off
  def sendRequest[T <: ApiRequest](apiRequest: T)(implicit request: Request[_]): WSResponse = {
    Logger.warn("Req = " + apiRequest.requestUrl)

    val fut = apiRequest match {
      case req: GetApiRequest => http.url(req.requestUrl).withHeaders(getAuthHeaders: _*).get()
      // case req: PostApiRequest => http.url(req.requestUrl).post(req)
    }

    // TODO: This await was the only way this worked from a unit test, why doesn't this work without an await?
    val response = Await.result(fut, 30 seconds)

    Logger.warn("Res = " + response)

    if (response.status == OK) {
      Logger.warn("RESP OK = " + response)
    } else {
      Logger.warn("RESP NOT NOT NOT OK = " + response)
    }
    response
  }
  // scalastyle:on

  def getAuthHeaders: Seq[(String, String)] = {
    val basicAuthEncodedStr = Base64.getEncoder
      .encodeToString(s"${FrontendAppConfig.launchpadApiKey}:passworddoesnotmatter".getBytes(StandardCharsets.UTF_8))

    Logger.warn("Basic = " + basicAuthEncodedStr)

    Seq(
      "Authorization" -> s"Basic $basicAuthEncodedStr",
      "Content-type" -> "application/x-www-form-urlencoded"
    )
  }
}
