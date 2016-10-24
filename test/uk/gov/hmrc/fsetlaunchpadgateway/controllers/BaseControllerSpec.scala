package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{ JsValue, Json, Writes }
import play.api.mvc.{ AnyContentAsEmpty, Results }
import play.api.test.{ FakeHeaders, FakeRequest }

abstract class BaseControllerSpec extends PlaySpec with MockitoSugar with Results with ScalaFutures {
  def fakeRequest[T](request: T)(implicit tjs: Writes[T]): FakeRequest[JsValue] =
    FakeRequest("", "", FakeHeaders(), Json.toJson(request)).withHeaders("Content-Type" -> "application/json")

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
}
