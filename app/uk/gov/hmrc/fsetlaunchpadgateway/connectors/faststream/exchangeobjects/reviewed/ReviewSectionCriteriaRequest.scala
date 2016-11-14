package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed

import play.api.libs.json.Json
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed.ReviewSectionCriteria

case class ReviewSectionCriteriaRequest(`type`: String, score: Option[Double])

object ReviewSectionCriteriaRequest {
  def fromExchange(callback: ReviewSectionCriteria): ReviewSectionCriteriaRequest = ReviewSectionCriteriaRequest(
    callback.`type`,
    callback.score.map(_.toDouble)
  )

  implicit val reviewCriteriaFormat = Json.format[ReviewSectionCriteriaRequest]
}
