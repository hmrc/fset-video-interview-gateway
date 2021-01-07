/*
 * Copyright 2020 HM Revenue & Customs
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

import akka.stream.Materializer
import com.kenshoo.play.metrics.MetricsFilter
import javax.inject.Inject
import play.api.Logger
import play.api.http.DefaultHttpFilters
import uk.gov.hmrc.fsetlaunchpadgateway.filters.{ FaststreamAuditFilter, FaststreamWhitelistFilter }
import uk.gov.hmrc.play.bootstrap.filters.{ CacheControlFilter, LoggingFilter }
import uk.gov.hmrc.play.bootstrap.frontend.filters.crypto.SessionCookieCryptoFilter
import uk.gov.hmrc.play.bootstrap.frontend.filters.deviceid.DeviceIdFilter
import uk.gov.hmrc.play.bootstrap.frontend.filters.{ FrontendFilters, HeadersFilter }

class FaststreamFilters @Inject() (
  metricsFilter: MetricsFilter,
  headersFilter: HeadersFilter,
  sessionCookieCryptoFilter: SessionCookieCryptoFilter,
  deviceIdFilter: DeviceIdFilter,
  loggingFilter: LoggingFilter,
  frontendAuditFilter: FaststreamAuditFilter,
  cacheControlFilter: CacheControlFilter)(implicit val materializer: Materializer) extends DefaultHttpFilters(
  metricsFilter,
  headersFilter,
  sessionCookieCryptoFilter,
  deviceIdFilter,
  loggingFilter,
  frontendAuditFilter,
  cacheControlFilter) {
  Logger.info("White list filter NOT enabled")
}

class ProductionFaststreamFilters @Inject() (
  metricsFilter: MetricsFilter,
  headersFilter: HeadersFilter,
  sessionCookieCryptoFilter: SessionCookieCryptoFilter,
  deviceIdFilter: DeviceIdFilter,
  loggingFilter: LoggingFilter,
  frontendAuditFilter: FaststreamAuditFilter,
  cacheControlFilter: CacheControlFilter,
  whitelistFilter: FaststreamWhitelistFilter)(implicit val materializer: Materializer) extends DefaultHttpFilters(
  metricsFilter,
  headersFilter,
  sessionCookieCryptoFilter,
  deviceIdFilter,
  loggingFilter,
  frontendAuditFilter,
  cacheControlFilter,
  whitelistFilter) {
  Logger.info("White list filter enabled")
}
