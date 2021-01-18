/*
 * Copyright 2021 HM Revenue & Customs
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

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.http.Status.{ BAD_REQUEST, OK }
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import uk.gov.hmrc.fsetlaunchpadgateway.config.{ FrontendAppConfig, WSHttpExternal }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate.{ CreateRequest, CreateResponse }
import uk.gov.hmrc.http.BadRequestException

class CandidateClientWithWiremockSpec extends BaseConnectorWithWiremockSpec {
  "create" should {
    val endpoint = s"/candidates"

    "return create response when OK" in new TestFixture {
      case class LaunchPadCreateResponse(val response: CreateResponse)
      implicit val createLaunchPadCreateResponseFormat = Json.format[LaunchPadCreateResponse]
      val response = CreateResponse("candiateId1", "customCandidateId")
      val launchPadResponse = LaunchPadCreateResponse(response)
      stubFor(post(urlPathEqualTo(endpoint))
        .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
        .withRequestBody(containing(
          "first_name=Joe&email=mail1%40mailinator.com&account_id=1&last_name=Bloggs&custom_candidate_id=customCandidateId"))
        .willReturn(
          aResponse().withStatus(OK).withBody(Json.toJson(launchPadResponse).toString())
        ))

      val request = CreateRequest(Some(1), "mail1@mailinator.com", Some("customCandidateId"), "Joe", "Bloggs")
      val result = client.create(request).futureValue

      result mustBe response
    }

    "throw BadRequestException when BAD_REQUEST" in new TestFixture {
      case class LaunchPadCreateResponse(val response: CreateResponse)
      implicit val createLaunchPadCreateResponseFormat = Json.format[LaunchPadCreateResponse]

      val response = CreateResponse("candiateId1", "customCandidateId")
      val launchPadResponse = LaunchPadCreateResponse(response)
      stubFor(post(urlPathEqualTo(endpoint))
        .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
        .withRequestBody(containing(
          "first_name=Joe&email=mail1%40mailinator.com&account_id=1&last_name=Bloggs&custom_candidate_id=customCandidateId"))
        .willReturn(
          aResponse().withStatus(BAD_REQUEST)
        )
      )

      val request = CreateRequest(Some(1), "mail1@mailinator.com", Some("customCandidateId"), "Joe", "Bloggs")
      val result = client.create(request).failed.futureValue

      // TODO: The test should be expecting a CreateException, however CandidateClient.create method
      // calls a buggy Client.postWithResponseAsOrThrow method, that is assuming that when there is an error in
      // calling launchpad (BAD_REQUEST, INTERNAL_SERVER_ERROR, etc), the implicit HttpReads
      // will always return a response with the status code, but it is throwing an exception instead.
      // Surprinsingly, this expected behaviour is the way the new version of HttpReads (in hmrc http-verbs project)
      // works.
      // After a discussion, we have decided to leave it as it is because, in real life, it is working fine:
      // even though we do not generate the CreateException, we generate an Exception (BadRequestException),
      // and the code in ApplicationController is not distinguishing types of exceptions in the error handling code.
      // In the future, if we sort out the problem and we use the new version of HttpVerbs, we should switch the comment
      // between this two assertions.
      //result mustBe an[CreateException]
      result mustBe an[BadRequestException]

    }
  }

  trait TestFixture extends BaseConnectorTestFixture {
    val mockConfig = new FrontendAppConfig(mockConfiguration, mockEnvironment) {
      override lazy val launchpadApiConfig =
        LaunchpadApiConfig("extension@mailinator.com", "key", s"http://localhost:$wireMockPort", 1, "http://localhost/callback")
    }
    val ws = app.injector.instanceOf(classOf[WSClient])
    val http = new WSHttpExternal(ws, app)
    val client = new CandidateClient(http, mockConfig)
  }
}

