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

import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.{ Application, Logging }
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

// This is a manual test class for trying new methods against the launchpad API
class ManualClient extends PlaySpec with Logging {

  "Testing" should {
    "Client test" ignore {

      val application: Application = new GuiceApplicationBuilder()
        .build()

      val appConfig = application.injector.instanceOf(classOf[FrontendAppConfig])
      val accountClient = application.injector.instanceOf(classOf[AccountClient])

      implicit val fakeRequest = FakeRequest()

      val accountId = Some(appConfig.launchpadApiConfig.accountId)

      logger.warn("Sending request...")

      val theRequest = accountClient.getSpecific(accountId.get)

      // TODO: This should be a mapped future
      val response = Await.result(theRequest, 30 seconds)

      logger.warn("Response = " + response.body + "\n\n")

      assert(true)
    }
  }
}
/*
List all interviews
val theRequest = InterviewClient.list(Some(FrontendAppConfig.launchpadApiConfig.accountId))
 */
/*
Register a new callback endpoint for an account
 val theRequest = AccountClient.updateAccount(accountId.get, AccountClient.UpdateRequest(
        Some(FrontendAppConfig.launchpadApiConfig.callbackUrl)
      ))
 */
/*
Get a specific sub-account's information
val theRequest = AccountClient.getSpecific(accountId.get)
 */
/*
Invite candidate
      val theRequest = InterviewClient.seamlessLoginInvite(
        accountId,
        FrontendAppConfig.launchpadApiConfig.testInterviewId,
        InterviewClient.SeamlessLoginInviteRequest(
          accountId,
          FrontendAppConfig.launchpadApiConfig.testCandidateId,
          Some("CSR_CUSTOM_INVITE_REFERENCE_HENRI_1"),
          None,
          None
        )
      )
 */
/*
Create a candidate
val theRequest = CandidateClient.create(
        CandidateClient.CreateRequest(
          accountId,
          "REPLACEEMAIL",
          Some("CUSTOM_CSR_ID_TO_KNOW_WHO_IS_WHO"),
          "TestFirst",
          "TestLast"
        )
      )
 */

/*
Create a new interview
val theRequest = InterviewClient.create(
        InterviewClient.CreateRequest(
          accountId,
          "API Test Interview",
          Some("These are some comments"),
          None,
          "There will be some responsibilities, and great power will come with them",
          None,
          Some(false),
          Some("7"), // 7 day deadline from time of invite
          Some("This is an introductory message"),
          Some("This is a closing message"),
          Some(15),
          "http://www.bbc.co.uk",
          Some("Special CSR Continue Button Text"),
          None,
          None,
          List(
            Question("Are you happy with this 1st question?", None, None),
            Question("When is a lemon, no longer a lemon?", Some(5), None),
            Question("Are you happy with this 3rd question?", Some(10), Some(15)),
            Question("Are you happy with this 4th question?", None, Some(20))
          )
        )
      )
      */
/* val theRequest = appClient.create(
CreateRequest(
accountId,
"Civil Service Faststream",
Some("Faststream"),
None,
Some("REPLACEMEAIL"),
Some(true),
Some(true),
Some(true),
"http://civilserviceresourcing.havaspeople.com/uploads/1/6/7/6/16763152/517385.png",
None,
"<CALLBACK URL HERE>",
None,
Some(true),
None
)) */
