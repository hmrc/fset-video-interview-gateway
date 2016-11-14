package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback.reviewed

import play.api.libs.json.Json

case class ReviewSectionCriteria(text: String, `type`: String, score: Option[String])

object ReviewSectionCriteria {
  implicit val reviewCriteriaFormat = Json.format[ReviewSectionCriteria]
}
