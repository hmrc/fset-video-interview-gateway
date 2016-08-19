package connectors.launchpad

import java.nio.charset.StandardCharsets
import java.util.Base64

import play.api.Logger
import play.api.libs.ws.WS
import uk.gov.hmrc.fsetlaunchpadgateway.FrontendAppConfig

trait Client {
  val http: WS.type

  val path: String

  val apiBaseUrl = FrontendAppConfig.launchpadApiBaseUrl

  private def accountIdQueryParam(accountId: Option[Int]) = accountId.map { accId =>
    val prefix = "?"
    s"${prefix}accountId=${accId.toString}"
  }.getOrElse("")

  protected def getRequestUrl(accountId: Option[Int]): String = {
    val accountIdStr = accountIdQueryParam(accountId)
    s"$apiBaseUrl/$path$accountIdStr"
  }

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

