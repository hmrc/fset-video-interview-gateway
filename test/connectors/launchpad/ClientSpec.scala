
package connectors.launchpad

import org.scalatestplus.play._
import connectors.launchpad.request.{ AccountRequestFactory, InterviewRequestFactory }
import play.api.Logger
import play.api.test.FakeRequest
import uk.gov.hmrc.fsetlaunchpadgateway.FrontendAppConfig
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global

class ClientSpec extends UnitSpec with OneServerPerTest {

  "Testing" should {
    "Client test" in {
      val client = Client

      val accountId = Some(FrontendAppConfig.launchpadApiAccountId)

      // TODO: Create an account
      val theRequest = AccountRequestFactory.ListRequest(accountId)

      implicit val fakeRequest = FakeRequest()

      Logger.warn("Sending request...")

      // TODO: This should be a mapped future
      val response = client.sendRequest(theRequest)

      Logger.warn("Response = " + response.body + "\n\n")

      assert(true)
    }
  }

}
