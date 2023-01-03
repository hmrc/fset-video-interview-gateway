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

package uk.gov.hmrc.fsetlaunchpadgateway.config

import java.util.Base64

import org.scalatest.TestData
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import play.api.{ Application, Environment, Mode }

class FaststreamAllowlistFilterConfigSpec extends PlaySpec with GuiceOneAppPerTest {

  val dummyIP1 = "11.22.33.44"
  val dummyIP2 = "93.00.33.33"

  override def newAppForTest(td: TestData): Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Test, path = new java.io.File(".")))
    .configure(Map(
      "whitelistExcludedCalls" -> Base64.getEncoder.encodeToString("/ping/ping,/healthcheck".getBytes),
      "whitelist" -> Base64.getEncoder.encodeToString(s"$dummyIP1".getBytes),
      "play.http.filters" -> "uk.gov.hmrc.fsetlaunchpadgateway.config.ProductionFaststreamFilters"
    ))
    .build()

  "FrontendAppConfig" must {
    "return a valid config item" when {
      "the allowlist exclusion paths are requested" in {
        val environment = Environment.simple(mode = Mode.Prod)
        val frontendAppConfig = new FrontendAppConfig(app.configuration, environment)
        frontendAppConfig.allowlistExcluded mustBe Seq("/ping/ping", "/healthcheck")
      }
      "the allowlist IPs are requested" in {
        val environment = Environment.simple(mode = Mode.Prod)
        val frontendAppConfig = new FrontendAppConfig(app.configuration, environment)
        frontendAppConfig.allowlist mustBe Seq(dummyIP1)
      }
    }
  }

  "AllowListFilter" must {
    "let requests pass" when {
      "coming from an IP in the allow list must work as normal" in {
        val request = FakeRequest(POST, "/fset-video-interview-gateway/faststream/callback")
          .withHeaders("True-Client-IP" -> dummyIP1)
        val Some(result) = route(app, request)

        status(result) mustBe BAD_REQUEST
      }

      "coming from an IP NOT in the allow list and not with an allow listed path must be redirected" in {
        val request = FakeRequest(GET, "/fset-video-interview-gateway/faststream/callback").withHeaders("True-Client-IP" -> dummyIP2)
        val Some(result) = route(app, request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("https://www.apply-civil-service-fast-stream.service.gov.uk/outage-fset-faststream/index.html")
      }

      "coming from an IP NOT in the allow list, but with an allow listed path must work as normal" in {
        val request = FakeRequest(GET, "/ping/ping").withHeaders("True-Client-IP" -> dummyIP2)
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
