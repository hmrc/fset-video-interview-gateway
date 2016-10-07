package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

import play.api.Logger
import play.api.libs.ws.{ WS, WSResponse }
import play.api.Play.current
import uk.gov.hmrc.fsetlaunchpadgateway.WSHttp
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.InterviewClient.Question
import uk.gov.hmrc.play.http.{ HeaderCarrier, HttpResponse }
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import scala.concurrent.Future

trait Client {
  val http: WSHttp.type = WSHttp

  val path: String

  val apiBaseUrl = FrontendAppConfig.launchpadApiConfig.baseUrl

  // TODO: This whole method is grimy. Done quickly for the spike, needs to perhaps do something clever with QueryStringBindable in play instead
  def caseClassToTuples(cc: Product, isList: Boolean = false): Seq[(String, String)] = {
    val values = cc.productIterator

    val emptyKeys = mutable.Set[String]()
    val additionalQuestionKeys = mutable.ListBuffer[(String, String)]()

    val result = cc.getClass.getDeclaredFields.map(declaredField => declaredField.getName -> {
      val nextVal = values.next
      nextVal match {
        case Some(content) => content.toString
        case None =>
          emptyKeys.add(declaredField.getName); ""
        // ?questions[][text]=QUESTION+1&questions[][limit]=30&questions[][text]=QUESTION+2&questions[][limit]=60
        case x :: xs if x.isInstanceOf[Question] => {
          emptyKeys.add(declaredField.getName)
          nextVal.asInstanceOf[List[Question]].foreach { question =>
            additionalQuestionKeys +=
              ("questions[][text]" -> question.text)

            question.limit.map { limit =>
              additionalQuestionKeys +=
                ("questions[][limit]" -> limit.toString)
            }

            question.preparation_time.map { prep_time =>
              additionalQuestionKeys +=
                ("questions[][preparation_time]" -> prep_time.toString)
            }
          }
          ""
        }
        case anythingElse => anythingElse.toString
      }
    })

    (result ++ additionalQuestionKeys.reverse).filter { case (k, v) => !emptyKeys.contains(k) }
  }

  protected def getPostRequestUrl(optionalSuffix: String = ""): String = {
    s"$apiBaseUrl/$path$optionalSuffix"
  }

  protected def getGetRequestUrl(accountId: Option[Int]): String = {
    val accountIdStr = accountIdQueryParam(accountId)
    s"$apiBaseUrl/$path$accountIdStr"
  }

  // TODO: Not every call needs accountId, perhaps refactor this to the clients that use it only
  private def accountIdQueryParam(accountId: Option[Int]): String = accountId.map { accId =>
    val prefix = "?"
    s"${prefix}account_id=${accId.toString}"
  }.getOrElse("")

  protected def getAuthHeaders: Seq[(String, String)] = {
    val basicAuthEncodedStr = Base64.getEncoder
      .encodeToString(s"${FrontendAppConfig.launchpadApiConfig.key}:passworddoesnotmatter".getBytes(StandardCharsets.UTF_8))

    Seq(
      "Authorization" -> s"Basic $basicAuthEncodedStr",
      "Content-type" -> "application/x-www-form-urlencoded"
    )
  }

  def getHeaderCarrier: HeaderCarrier = new HeaderCarrier().withExtraHeaders(getAuthHeaders: _*)

  def get(url: String): Future[HttpResponse] = {
    Logger.warn(s"GETTING $url")
    // http.url(url).withHeaders(getAuthHeaders: _*).get()
    implicit val hc: HeaderCarrier = getHeaderCarrier
    http.GET(url)
  }

  def post(url: String, queryParams: Seq[(String, String)]): Future[HttpResponse] = {
    Logger.warn(s"POSTING $url with $queryParams")
    implicit val hc: HeaderCarrier = getHeaderCarrier
    val response = http.POSTForm(url, convertQueryParamsForTx(queryParams))
    response.map { x =>
      print("Raw response body = " + x.body + "\n\n")
    }
    response
  }

  def put(url: String, queryParams: Seq[(String, String)]): Future[HttpResponse] = {
    Logger.warn(s"PUTTING $url with $queryParams")
    implicit val hc: HeaderCarrier = getHeaderCarrier
    val qp = convertQueryParamsForTx(queryParams)
    val response = http.PUTForm(url, convertQueryParamsForTx(queryParams))
    // val response = http.PUT(url, qp)
    response.map { x =>
      print("Raw put response body = " + x.body + "\n\n")
    }
    response
  }

  private def convertQueryParamsForTx(queryParams: Seq[(String, String)]): Map[String, Seq[String]] =
    queryParams.map(pair => pair._1 -> Seq(pair._2))(collection.breakOut): Map[String, Seq[String]]
}

