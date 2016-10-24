package uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream

import uk.gov.hmrc.fsetlaunchpadgateway.config.WSHttp
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.FaststreamClient.CallbackException
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

object FaststreamClient extends FaststreamClient {
  override val http = WSHttp

  case class CallbackException(message: String) extends Exception(message)
}

trait FaststreamClient {

  val http: WSHttp

  import uk.gov.hmrc.fsetlaunchpadgateway.config.FrontendAppConfig.faststreamApiConfig._

  def setupProcessCallback(callback: SetupProcessCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT(s"${url.host}${url.base}$launchPadPrefix/setupProcessCallback", callback).map(okOrThrow)
  }

  def viewPracticeQuestionCallback(callback: ViewPracticeQuestionCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT(s"${url.host}${url.base}$launchPadPrefix/viewPracticeQuestion", callback).map(okOrThrow)
  }

  def questionCallback(callback: QuestionCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT(s"${url.host}${url.base}$launchPadPrefix/questionCallback", callback).map(okOrThrow)
  }

  def finalCallback(callback: FinalCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT(s"${url.host}${url.base}$launchPadPrefix/finalCallback", callback).map(okOrThrow)
  }

  def finishedCallback(callback: FinishedCallbackRequest)(implicit hc: HeaderCarrier): Future[Unit] = {
    val launchPadPrefix = getLaunchpadPrefix(callback.customInviteId)
    http.PUT(s"${url.host}${url.base}$launchPadPrefix/finishedCallback", callback).map(okOrThrow)
  }

  private def okOrThrow(response: HttpResponse) = {
    if (response.status != 200) {
      throw CallbackException(s"Response was not OK when forwarding callback to Faststream. Response: {$response.body}")
    }
  }

  private def getLaunchpadPrefix(inviteId: String) = "/launchpad/$inviteId"
}