package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects

import org.joda.time.LocalDate

abstract class BaseCallbackRequest(received: DateTime, candidateId: String, customCandidateId: String, interviewId: Int,
  customInterviewId: Option[String], customInviteId: String, status: String, deadline: LocalDate)
