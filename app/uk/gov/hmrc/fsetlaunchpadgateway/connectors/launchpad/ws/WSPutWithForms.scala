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

package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.ws

import play.api.http.HttpVerbs.{ PUT => PUT_VERB }
import uk.gov.hmrc.http.hooks.HookData
import uk.gov.hmrc.play.http.ws.{ WSHttpResponse, WSPut }

import scala.concurrent.ExecutionContext
import uk.gov.hmrc.http.{ HeaderCarrier, HttpPut, HttpReads, HttpResponse }

import scala.concurrent.Future

trait WSPutWithForms extends HttpPut with WSPut {
  def doFormPut(url: String, body: Map[String, Seq[String]])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    buildRequest(url).put(body).map(WSHttpResponse(_))
  }

  // scalastyle:off
  def PUTForm[O](url: String, body: Map[String, Seq[String]])(implicit rds: HttpReads[O], hc: HeaderCarrier, ec: ExecutionContext): Future[O] = {

    withTracing(PUT_VERB, url) {
      val httpResponse = doFormPut(url, body)
      executeHooks(url, PUT_VERB, Option(HookData.FromMap(body)), httpResponse)
      mapErrors(PUT_VERB, url, httpResponse).map(rds.read(PUT_VERB, url, _))
    }
  }
  // scalastyle:on
}
