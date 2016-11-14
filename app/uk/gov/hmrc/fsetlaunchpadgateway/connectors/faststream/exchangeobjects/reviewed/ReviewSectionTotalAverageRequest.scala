package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed.ReviewSectionTotalAverage

case class ReviewSectionTotalAverageRequest(`type`: String, scoreText: String, scoreValue: Double)

object ReviewSectionTotalAverageRequest {
  def fromExchange(callback: ReviewSectionTotalAverage): ReviewSectionTotalAverageRequest = ReviewSectionTotalAverageRequest(
    callback.`type`,
    callback.score_text,
    callback.score_value
  )
  implicit val reviewSectionTotalAverageFormat = Json.format[ReviewSectionTotalAverageRequest]
}
