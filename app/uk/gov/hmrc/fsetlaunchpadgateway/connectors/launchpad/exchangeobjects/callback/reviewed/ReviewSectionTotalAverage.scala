package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed

import play.api.libs.json.Json

case class ReviewSectionTotalAverage(`type`: String, score_text: String, score_value: Double)

object ReviewSectionTotalAverage {
  implicit val reviewSectionTotalAverageFormat = Json.format[ReviewSectionTotalAverage]
}
