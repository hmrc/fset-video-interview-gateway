package connectors.launchpad

import java.nio.charset.StandardCharsets
import java.util.Base64

import play.api.Logger
import play.api.libs.ws.WS
import play.api.Play.current
import uk.gov.hmrc.fsetlaunchpadgateway.FrontendAppConfig

trait Client {
  val http: WS.type

  val path: String

  val apiBaseUrl = FrontendAppConfig.launchpadApiBaseUrl

  def caseClassToTuples(cc: Product): Seq[(String, String)] = {
    val values = cc.productIterator
    cc.getClass.getDeclaredFields.map(_.getName -> values.next.toString)
  }

  private def accountIdQueryParam(accountId: Option[Int]) = accountId.map { accId =>
    val prefix = "?"
    s"${prefix}accountId=${accId.toString}"
  }.getOrElse("")

  protected def getPostRequestUrl = {
    s"$apiBaseUrl/$path"
  }

  protected def getGetRequestUrl(accountId: Option[Int]): String = {
    val accountIdStr = accountIdQueryParam(accountId)
    s"$apiBaseUrl/$path$accountIdStr"
  }

  protected def getAuthHeaders: Seq[(String, String)] = {
    val basicAuthEncodedStr = Base64.getEncoder
      .encodeToString(s"${FrontendAppConfig.launchpadApiKey}:passworddoesnotmatter".getBytes(StandardCharsets.UTF_8))

    Logger.warn("Basic = " + basicAuthEncodedStr)

    Seq(
      "Authorization" -> s"Basic $basicAuthEncodedStr",
      "Content-type" -> "application/x-www-form-urlencoded"
    )
  }

  def get(url: String) = {
    Logger.warn(s"GETTING $url")
    http.url(url).withHeaders(getAuthHeaders: _*).get()
  }

  def post(url: String, queryParams: Seq[(String, String)]) = {
    Logger.warn(s"POSTING $url")
    http.url(url).withQueryString(queryParams: _*).withHeaders(getAuthHeaders: _*).post("")
  }
}

