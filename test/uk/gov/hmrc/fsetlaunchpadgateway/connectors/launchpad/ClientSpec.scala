/*
 * Copyright 2023 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.http.Status.{ BAD_GATEWAY, OK }
import play.api.libs.json.Json
import play.api.{ Configuration, Environment }
import uk.gov.hmrc.fsetlaunchpadgateway.config.{ FrontendAppConfig, WSHttp }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData
import uk.gov.hmrc.http.{ HeaderCarrier, HttpReads, HttpResponse }

import scala.concurrent.{ ExecutionContext, Future }

case class ClientTestException(message: String, stringsToRemove: List[String]) extends SanitizedClientException(message, stringsToRemove)

case class ClientTestRequest(
  firstName: String,
  lastName: String,
  email: String
) extends ContainsSensitiveData {
  override def getSensitiveStrings: List[String] = List(firstName, email)
}

case class ClientTestResponse(testKey: String) extends ContainsSensitiveData {
  override def getSensitiveStrings: List[String] = Nil
}

object ClientTestResponse {
  implicit val clientTestResponseFormat = Json.format[ClientTestResponse]
}

class ClientSpec extends PlaySpec with MockitoSugar with ScalaFutures {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  "Posting a request" should {
    val sampleRequest = ClientTestRequest("Barry", "Johnson", "foo@bar.com")

    "return a properly parsed response when the request is a 200" in new PostTestFixture {
      val resp = successfulPostResponseTestClient.postWithResponseAsOrThrow[ClientTestResponse, ClientTestException](
        sampleRequest,
        "http://www.test.com",
        ClientTestException
      ).futureValue

      resp.testKey mustBe "this is a successful message"
    }

    "throw an exception when a response with no 'response' key is returned" in new PostTestFixture {
      val ex = malformedPostResponseTestClient.postWithResponseAsOrThrow[ClientTestResponse, ClientTestException](
        sampleRequest,
        "http://www.test.com",
        ClientTestException
      ).failed.futureValue

      ex mustBe a[ClientTestException]
      ex.getMessage must include("Unexpected response from Launchpad")
    }

    "throw an exception when a response with a response key but malformed contents is returned" in new PostTestFixture {
      val ex = malformedPostResponseContentTestClient.postWithResponseAsOrThrow[ClientTestResponse, ClientTestException](
        sampleRequest,
        "http://www.test.com",
        ClientTestException
      ).failed.futureValue

      ex mustBe a[ClientTestException]
      ex.getMessage must include("Unexpected response from Launchpad")
    }

    "throw a properly sanitised exception when an unexpected response is returned" in new PostTestFixture {
      val ex = malformedPostResponseTestClient.postWithResponseAsOrThrow[ClientTestResponse, ClientTestException](
        sampleRequest,
        "http://www.test.com",
        ClientTestException
      ).failed.futureValue

      ex mustBe a[ClientTestException]
      ex.getMessage must include("******")
      sampleRequest.getSensitiveStrings.foreach { sensitiveString =>
        ex.getMessage must not include sensitiveString
      }
    }

    "throw an exception when a non-200 response is returned" in new PostTestFixture {
      val ex = non200TestClient.postWithResponseAsOrThrow[ClientTestResponse, ClientTestException](
        sampleRequest,
        "http://www.test.com",
        ClientTestException
      ).failed.futureValue

      ex mustBe a[ClientTestException]
      ex.getMessage must include("Received a 502 code from Launchpad")
    }

    "throw a properly sanitised exception when a non-200 response is returned" in new PostTestFixture {
      val ex = non200TestClient.postWithResponseAsOrThrow[ClientTestResponse, ClientTestException](
        sampleRequest,
        "http://www.test.com",
        ClientTestException
      ).failed.futureValue

      ex mustBe a[ClientTestException]
      ex.getMessage must include("Received a 502 code from Launchpad")
      ex.getMessage must include("******")
      sampleRequest.getSensitiveStrings.foreach { sensitiveString =>
        ex.getMessage must not include sensitiveString
      }
    }
  }

  trait PostTestFixture {

    implicit val hc = new HeaderCarrier()

    val wsHttpMock: WSHttp = mock[WSHttp]

    lazy val successfulPostResponseTestClient = makeClient {
      when(wsHttpMock.POSTForm(any(), any(), any())(any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext])).thenReturn {
        Future.successful(
          HttpResponse(
            OK,
            Json.parse(
              """
                | {
                |   "response": {
                |     "testKey": "this is a successful message"
                |   }
                | }
              """.stripMargin),
            Map.empty[String, Seq[String]]
          )
        )
      }
    }

    lazy val malformedPostResponseTestClient = makeClient {
      when(wsHttpMock.POSTForm(any(), any(), any())(any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext])).thenReturn {
        Future.successful(
          HttpResponse(
            OK,
            Json.parse(
              """
                | {
                |   "unexpected": "response"
                | }
              """.stripMargin),
            Map.empty[String, Seq[String]]
          )
        )
      }
    }

    lazy val malformedPostResponseContentTestClient = makeClient {
      when(wsHttpMock.POSTForm(any(), any(), any())(any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext])).thenReturn {
        Future.successful(
          HttpResponse(
            OK,
            Json.parse(
              """
                | {
                |   "response": {
                |     "foo": "bar"
                |   }
                | }
              """.stripMargin),
            Map.empty[String, Seq[String]]
          )
        )
      }
    }

    lazy val non200TestClient = makeClient {
      when(wsHttpMock.POSTForm(any(), any(), any())(any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext])).thenReturn {
        Future.successful(
          HttpResponse(
            BAD_GATEWAY,
            Json.parse(
              """
                | {
                |   "response": "error"
                | }
              """.stripMargin),
            Map.empty[String, Seq[String]]
          )
        )
      }
    }

    private def makeClient(mockSetup: => Unit): Client = {
      mockSetup
      val mockConfiguration = mock[Configuration]
      val mockEnvironment = mock[Environment]
      val mockConfig = new FrontendAppConfig(mockConfiguration, mockEnvironment) {
        override lazy val launchpadApiConfig = LaunchpadApiConfig("extension", "key", "baseurl", 1, "http://localhost")
      }
      new Client(wsHttpMock, "testclient", mockConfig) {
      }
    }
  }
}
