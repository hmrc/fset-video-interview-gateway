
package connectors.launchpad

import connectors.launchpad.InterviewClient.Question
import org.scalatestplus.play._
import play.api.Logger
import play.api.test.FakeRequest
import uk.gov.hmrc.fsetlaunchpadgateway.FrontendAppConfig
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Await
import scala.concurrent.duration._
import language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

class ClientSpec extends UnitSpec with OneServerPerTest {

  "Testing" should {
    "Client test" in {

      implicit val fakeRequest = FakeRequest()

      val appClient = AccountClient
      val accountId = Some(FrontendAppConfig.launchpadApiAccountId)

      Logger.warn("Sending request...")

      val theRequest = InterviewClient.create(
        InterviewClient.CreateRequest(
          accountId,
          "API Test Interview",
          Some("These are some comments"),
          None,
          "There will be some responsibilities, and great power will come with them",
          None,
          Some(false),
          None,
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

      // TODO: This should be a mapped future
      val response = Await.result(theRequest, 30 seconds)

      Logger.warn("Response = " + response.body + "\n\n")

      assert(true)
    }
  }

}

/*
      {"account_id": REMOVED,"company_name":"CSR - Test","sms_company_name":null,"company_comment":null,"email":"***REMOVED***","first_name":"Henry","last_name":"Charge","email_employers":true,"email_applicants":true,"send_feedback_email":true,"logo_url":null,"banner_url":null,"callback_url":"","status_frequency":null,"json_callback":true,"timezone":"London"}}
       */
/* Create new account
      appClient.create(
      CreateRequest(
        accountId,
        "Civil Service Faststream",
        Some("Faststream"),
        None,
        Some("***REMOVED***"),
        Some(true),
        Some(true),
        Some(true),
        "http://civilserviceresourcing.havaspeople.com/uploads/1/6/7/6/16763152/517385.png",
        None,
        "http://NOT_IMPLEMENTED",
        None,
        Some(true),
        None
      )
    )*/
