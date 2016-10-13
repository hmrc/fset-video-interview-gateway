package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview

import play.api.libs.json.Json

case class Question(
  text: String,
  limit: Option[Int],
  preparation_time: Option[Int]
)

object Question {
  implicit val questionFormat = Json.format[Question]
}
