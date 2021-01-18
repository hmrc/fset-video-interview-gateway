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

package uk.gov.hmrc.fsetlaunchpadgateway.filters

import java.util.Base64

import akka.stream.Materializer
import javax.inject.{ Inject, Singleton }
import play.api.Configuration
import play.api.mvc.{ Call, EssentialFilter, RequestHeader, Result }
import uk.gov.hmrc.whitelist.AkamaiWhitelistFilter

import scala.concurrent.Future

// We are using our own WhiteList filter instead of using this one
// https://github.com/hmrc/bootstrap-play/blob/
// master/bootstrap-frontend-play-26/src/main/scala/uk/gov/hmrc/play/bootstrap/frontend/filters/WhitelistFilter.scala
// They are slightly different, specially regarding the pathPrefixesToExclude
@Singleton
class FaststreamWhitelistFilter @Inject() (
  val configuration: Configuration,
  val mat: Materializer)
  extends AkamaiWhitelistFilter with EssentialFilter {

  // Whitelist Configuration
  private def whitelistConfig(key: String): Seq[String] =
    Some(new String(Base64.getDecoder.decode(configuration.getOptional[String](key).getOrElse("")), "UTF-8"))
      .map(_.split(",")).getOrElse(Array.empty).toSeq

  override def noHeaderAction(
    f: (RequestHeader) => Future[Result],
    rh: RequestHeader
  ): Future[Result] = { f(rh) }

  // List of IP addresses
  override def whitelist: Seq[String] = whitelistConfig("whitelist")

  // Es. /ping/ping,/admin/details
  override def excludedPaths: Seq[Call] = whitelistConfig("whitelistExcludedCalls").map {
    path => Call("GET", path)
  }

  override def destination: Call = Call(
    "GET",
    "https://www.apply-civil-service-fast-stream.service.gov.uk/outage-fset-faststream/index.html")
}
