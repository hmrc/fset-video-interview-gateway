/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import play.api.http.Status._
import play.api.libs.json.Format
import uk.gov.hmrc.fsetlaunchpadgateway.config.{ FrontendAppConfig, WSHttp }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview.Question
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.collection.mutable
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

object Client {
  abstract class SanitizedClientException(message: String, stringsToRemove: List[String])
    extends Exception(sanitizeLog(message, stringsToRemove))

  def sanitizeLog(stringToSanitize: String, stringsToRemove: List[String]): String = {
    stringsToRemove.foldLeft(stringToSanitize)(_.replace(_, "******"))
  }
}

abstract class Client(http: WSHttp, val path: String, config: FrontendAppConfig)(implicit val ec: ExecutionContext) {
  val apiBaseUrl: String = config.launchpadApiConfig.baseUrl

  // TODO: This whole method is grimy. Done quickly for the spike, needs to perhaps do something clever with QueryStringBindable in play instead
  def caseClassToTuples(cc: Product): Seq[(String, String)] = {
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
        case x :: _ if x.isInstanceOf[Question] =>
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
        case anythingElse => anythingElse.toString
      }
    })

    (result ++ additionalQuestionKeys.reverse).filter { case (k, _) => !emptyKeys.contains(k) }
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
      .encodeToString(s"${config.launchpadApiConfig.key}:passworddoesnotmatter".getBytes(StandardCharsets.UTF_8))

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
    // TODO: This code is assuming that when there is an error calling launchpad
    // (BAD_REQUEST, INTERNAL_SERVER_ERROR, etc), the implicit HttpReads
    // will always return a response with the status code, but it is throwing an exception instead.
    // Surprisingly, this expected behaviour is the way the new version of HttpReads (in hmrc http-verbs project)
    // works.
    // After a discussion, we have decided to leave it as it is because, in real life, it is working fine:
    // even though we do not generate the CreateException, we generate an Exception (BadRequestException),
    // and the code in ApplicationController is not distinguishing types of exceptions in the error handling code.
    // In the future we should consider:
    // -Using the new version of HttpVerbs by adding this code:
    // import uk.gov.hmrc.http.HttpReads.Implicits._
    // and adding [HttpResponse] to http.GET(url), http.POSTForm, http.PUTForm and then add code to generate an exception
    // in case httpResponse.statusCode is not OK
    // More information in https://github.com/hmrc/http-verbs
    post(postUrl, caseClassToTuples(request)).map { response =>
      if (response.status == OK) {
        Try(response.json.\\("response").head.as[R]) match {
          case Success(resp) => resp
          case Failure(ex) => throw exceptionOnFailure(s"Unexpected response from Launchpad when calling $postUrl. Response body was:" +
            s"${response.body}. Request: $request. Caused by exception: $ex .", request.getSensitiveStrings)
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
    http.GET[HttpResponse](url)
  }

  def post(url: String, queryParams: Seq[(String, String)]): Future[HttpResponse] = {
    implicit val hc: HeaderCarrier = getHeaderCarrier
    http.POSTForm[HttpResponse](url, convertQueryParamsForTx(queryParams))
  }

  def put(url: String, queryParams: Seq[(String, String)]): Future[HttpResponse] = {
    implicit val hc: HeaderCarrier = getHeaderCarrier
    http.PUTForm[HttpResponse](url, convertQueryParamsForTx(queryParams))
  }

  private def convertQueryParamsForTx(queryParams: Seq[(String, String)]): Map[String, Seq[String]] =
    queryParams.map {
      case (key, value) => key -> Seq(value)
    }(collection.breakOut): Map[String, Seq[String]]
}
