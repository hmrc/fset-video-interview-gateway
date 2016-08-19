
package connectors.launchpad

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

      val client = AccountClient
      val accountId = Some(FrontendAppConfig.launchpadApiAccountId)

      Logger.warn("Sending request...")

      val theRequest = client.list(accountId)

      // TODO: This should be a mapped future
      val response = Await.result(theRequest, 30 seconds)

      Logger.warn("Response = " + response.body + "\n\n")

      assert(true)
    }
  }

}
