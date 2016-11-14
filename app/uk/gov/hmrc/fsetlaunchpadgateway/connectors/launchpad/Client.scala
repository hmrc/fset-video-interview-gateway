package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import play.api.http.Status._
import java.nio.charset.StandardCharsets
import java.util.Base64

import play.api.Logger
import play.api.libs.json.Format
import uk.gov.hmrc.fsetlaunchpadgateway.config.WSHttp
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview.Question
import uk.gov.hmrc.play.http.{ HeaderCarrier, HttpResponse }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

object Client {
  abstract class SanitizedClientException(message: String, stringsToRemove: List[String])
    extends Exception(sanitizeLog(message, stringsToRemove))

  def sanitizeLog(stringToSanitize: String, stringsToRemove: List[String]): String = {
    stringsToRemove.foldLeft(stringToSanitize)(_.replace(_, "******"))
  }
}

trait Client {
  val http: WSHttp = WSHttp

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

  def postWithResponseAsOrThrow[R <: ContainsSensitiveData, E <: SanitizedClientException](
    request: Product with ContainsSensitiveData,
    postUrl: String,
    exceptionOnFailure: => (String, List[String]) => E
  )(implicit jsonFormat: Format[R]): Future[R] = {

    post(postUrl, caseClassToTuples(request)).map { response =>
      if (response.status == OK) {
        Try(response.json.\\("response").head.as[R]) match {
          case Success(resp) => resp
          case Failure(ex) => throw exceptionOnFailure(s"Unexpected response from Launchpad when calling $postUrl. Body was:" +
            s"${response.body}. Request: $request", request.getSensitiveStrings)
        }
      } else {
        throw exceptionOnFailure(s"Received a ${response.status} code from Launchpad when calling $postUrl. " +
          s"Response: ${response.body}. Request: $request", request.getSensitiveStrings)
      }
    }
  }

  def getHeaderCarrier: HeaderCarrier = new HeaderCarrier().withExtraHeaders(getAuthHeaders: _*)

  def get(url: String): Future[HttpResponse] = {
    // http.url(url).withHeaders(getAuthHeaders: _*).get()
    implicit val hc: HeaderCarrier = getHeaderCarrier
    http.GET(url)
  }

  def post(url: String, queryParams: Seq[(String, String)]): Future[HttpResponse] = {
    implicit val hc: HeaderCarrier = getHeaderCarrier
    http.POSTForm(url, convertQueryParamsForTx(queryParams))
  }

  def put(url: String, queryParams: Seq[(String, String)]): Future[HttpResponse] = {
    implicit val hc: HeaderCarrier = getHeaderCarrier
    val qp = convertQueryParamsForTx(queryParams)
    http.PUTForm(url, convertQueryParamsForTx(queryParams))
  }

  private def convertQueryParamsForTx(queryParams: Seq[(String, String)]): Map[String, Seq[String]] =
    queryParams.map(pair => pair._1 -> Seq(pair._2))(collection.breakOut): Map[String, Seq[String]]
}

