
package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad

import org.scalatestplus.play._
import play.api.Logger
import play.api.test.FakeRequest
import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.account.{ CreateRequest, UpdateRequest }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.candidate.ExtendDeadlineRequest
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview.SeamlessLoginInviteRequest
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.account.UpdateRequest
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Await
import scala.concurrent.duration._
import language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

// This is a manual test class for trying new methods against the launchpad API
class ManualClient extends UnitSpec with OneServerPerTest {

  "Testing" should {
    "Client test" ignore {

      implicit val fakeRequest = FakeRequest()

      val accountId = Some(FrontendAppConfig.launchpadApiConfig.accountId)

      Logger.warn("Sending request...")

      val theRequest = AccountClient.updateAccount(
        accountId.get, UpdateRequest(
        Some(FrontendAppConfig.launchpadApiConfig.callbackUrl)
      ))

      // TODO: This should be a mapped future
      val response = Await.result(theRequest, 30 seconds)

      Logger.warn("Response = " + response.body + "\n\n")

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
"https://www-staging.tax.service.gov.uk/fset-video-interview-gateway/callback",
None,
Some(true),
None
)) */
