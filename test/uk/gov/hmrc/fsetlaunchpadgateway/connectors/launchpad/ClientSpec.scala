
package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import org.scalatestplus.play._
import org.mockito.Matchers.{ eq => eqTo, _ }
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{ Json, Reads }
import uk.gov.hmrc.fsetlaunchpadgateway.config.WSHttp
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.Client.SanitizedClientException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.ContainsSensitiveData

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._
import scala.language.postfixOps
import uk.gov.hmrc.http.{ HeaderCarrier, HttpReads, HttpResponse }

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

class ClientSpec extends PlaySpec with OneServerPerTest with MockitoSugar with ScalaFutures {

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
      when(wsHttpMock.POSTForm(any(), any())(any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext])).thenReturn {
        Future.successful(
          HttpResponse(200, Some(
            Json.parse(
              """
                | {
                |   "response": {
                |     "testKey": "this is a successful message"
                |   }
                | }
              """.stripMargin)
          ))
        )
      }
    }

    lazy val malformedPostResponseTestClient = makeClient {
      when(wsHttpMock.POSTForm(any(), any())(any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext])).thenReturn {
        Future.successful(
          HttpResponse(200, Some(
            Json.parse(
              """
                | {
                |   "unexpected": "response"
                | }
              """.stripMargin)
          ))
        )
      }
    }

    lazy val malformedPostResponseContentTestClient = makeClient {
      when(wsHttpMock.POSTForm(any(), any())(any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext])).thenReturn {
        Future.successful(
          HttpResponse(200, Some(
            Json.parse(
              """
                | {
                |   "response": {
                |     "foo": "bar"
                |   }
                | }
              """.stripMargin)
          ))
        )
      }
    }

    lazy val non200TestClient = makeClient {
      when(wsHttpMock.POSTForm(any(), any())(any[HttpReads[HttpResponse]](), any[HeaderCarrier](), any[ExecutionContext])).thenReturn {
        Future.successful(
          HttpResponse(502, Some(
            Json.parse(
              """
                | {
                |   "response": "error"
                | }
              """.stripMargin)
          ))
        )
      }
    }

    private def makeClient(mockSetup: => Unit): Client = {
      mockSetup
      new Client {
        override val http = wsHttpMock
        override val path = "testclient"
      }
    }
  }
}
