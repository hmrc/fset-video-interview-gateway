package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects.callback

import org.joda.time.LocalDate

abstract class BaseCallback(candidate_id: String, custom_candidate_id: String, interview_id: Int,
  custom_interview_id: String, custom_invite_id: String, status: String, deadline: LocalDate)
