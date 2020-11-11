package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback

import org.joda.time.LocalDate
import uk.gov.hmrc.fsetlaunchpadgateway.models.commands.FaststreamImplicits._

abstract class BaseCallback(candidate_id: String, custom_candidate_id: String, interview_id: Int,
  custom_interview_id: Option[String], custom_invite_id: String, status: String, deadline: LocalDate)
