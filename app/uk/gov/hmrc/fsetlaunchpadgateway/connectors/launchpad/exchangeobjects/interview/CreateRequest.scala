package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.interview

import play.api.libs.json.Json

case class CreateRequest(
  account_id: Option[Int],
  title: String,
  comments: Option[String],
  custom_interview_id: Option[String],
  responsibilities: String, // Job responsibilities
  qualifications: Option[String],
  rerecord: Option[Boolean],
  deadline: Option[String],
  introduction_message: Option[String],
  closing_message: Option[String],
  time_limit: Option[Int],
  redirect_url: String,
  redirect_button_name: Option[String],
  show_redirect_button: Option[Boolean],
  default_language: Option[String],
  questions: List[Question]

) {
  def isValid: Boolean = {
    deadline.forall(theDeadline =>
      // Candidate specific deadline
      theDeadline.matches("\\d+") ||
        // Hard date deadline
        theDeadline.matches("\\d{4}-\\d{2}-\\d{2}")
    ) &&
      time_limit.forall(List(15, 30, 45, 60).contains) &&
      default_language.forall(List("en").contains)
  }
}

object CreateRequest {
  implicit val createRequestFormat = Json.format[CreateRequest]
}
