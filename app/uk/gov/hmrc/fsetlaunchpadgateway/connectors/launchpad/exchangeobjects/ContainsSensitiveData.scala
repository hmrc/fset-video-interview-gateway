package uk.gov.hmrc.fsetlaunchpadgateway.connectors.launchpad.exchangeobjects

abstract class ContainsSensitiveData {
  def getSensitiveStrings: List[String]
}
