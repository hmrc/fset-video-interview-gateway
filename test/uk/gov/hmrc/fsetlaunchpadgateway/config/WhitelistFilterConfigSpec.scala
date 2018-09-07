package uk.gov.hmrc.fsetlaunchpadgateway.config

import java.util.Base64

import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }
import play.api.{ Application, Mode }
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._

import language.implicitConversions

class WhitelistFilterConfigSpec extends PlaySpec with OneAppPerSuite {

  val dummyIP1 = "11.22.33.44"
  val dummyIP2 = "93.00.33.33"

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      "whitelistExcludedCalls" -> Base64.getEncoder.encodeToString("/ping/ping,/healthcheck".getBytes),
      "whitelist" -> Base64.getEncoder.encodeToString("dummyIP1".getBytes)
    ).global(TestFrontendGlobal).in(Mode.Test).build()

  "FrontendAppConfig" must {
    "return a valid config item" when {
      "the whitelist exclusion paths are requested" in {
        FrontendAppConfig.whitelistExcluded mustBe Seq("/ping/ping", "/healthcheck")
      }
      "the whitelist IPs are requested" in {
        FrontendAppConfig.whitelist mustBe Seq("dummyIP1")
      }
    }
  }

  "ProductionFrontendGlobal" must {
    "let requests past" when {
      "coming from an IP in the white list must work as normal" in {
        val request = FakeRequest(POST, "/fset-video-interview-gateway/faststream/callback").withHeaders("True-Client-IP" -> "dummyIP1")
        val Some(result) = route(app, request)

        status(result) mustBe BAD_REQUEST
      }

      "coming from a IP NOT in the white-list and not with a white-listed path must be redirected" in {
        val request = FakeRequest(POST, "/fset-video-interview-gateway/faststream/callback").withHeaders("True-Client-IP" -> "dummyIP2")
        val Some(result) = route(app, request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("https://www.apply-civil-service-fast-stream.service.gov.uk/outage-fset-faststream/index.html")
      }

      "coming from an IP NOT in the white-list, but with a white-listed path must work as normal" in {
        val request = FakeRequest(GET, "/ping/ping").withHeaders("True-Client-IP" -> "dummyIP2")
        val Some(result) = route(app, request)

        status(result) mustBe OK
      }

      "coming without an Akamai IP header must succeed (like an internal service calling the gateway)" in {
        val request = FakeRequest(POST, "/fset-video-interview-gateway/faststream/callback")
        val Some(result) = route(app, request)

        status(result) mustBe BAD_REQUEST
      }
    }
  }
}
