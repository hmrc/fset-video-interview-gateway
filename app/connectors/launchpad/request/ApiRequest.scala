package connectors.launchpad.request

import uk.gov.hmrc.fsetlaunchpadgateway.FrontendAppConfig
import play.api.libs.json._

abstract class PostApiRequest(path: String, accountId: Option[Int]) extends ApiRequest(path, accountId) {
}

abstract class GetApiRequest(path: String, accountId: Option[Int]) extends ApiRequest(path, accountId) {
}

abstract class ApiRequest(path: String, accountId: Option[Int]) {
  val accountIdStr = accountId.map { accId =>
    val prefix = "?"
    s"${prefix}accountId=${accId.toString}"
    // TODO: serialisation of params
  }.getOrElse("")

  val apiBaseUrl = FrontendAppConfig.launchpadApiBaseUrl

  val requestUrl = s"$apiBaseUrl/$path$accountIdStr"
}
