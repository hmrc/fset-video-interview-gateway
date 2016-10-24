package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ AnyContentAsEmpty, AnyContentAsJson }
import play.api.test.Helpers._
import play.api.test.{ FakeHeaders, FakeRequest, Helpers }

class CallbackControllerSpec extends BaseControllerSpec with OneAppPerSuite {
  "Callback Controller#present" should {
    "correctly parse and reply to Setup Process Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(setupProcessJson))

      status(result) mustBe OK
    }

    "correctly parse and reply to View Practice Question Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(viewPracticeQuestionJson))

      status(result) mustBe OK
    }

    "correctly parse and reply to Question Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(questionCallbackJson))

      status(result) mustBe OK
    }

    "correctly parse and reply to Final Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(finalCallbackJson))

      status(result) mustBe OK
    }

    "correctly parse and reply to Finished Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(finishedCallbackJson))

      status(result) mustBe OK
    }

    "when parsing valid but unrecognisable json, but with a valid status key, return a bad request" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(unrecogniseableJsonValidStatus))
    }

    "when parsing valid but unrecognisable json, return a bad request" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(unrecogniseableJson))
    }

    "when parsing invalid json, return a bad request" in new TestFixture {
      val result = controller.present()(makeCallbackPostRequest(invalidJson))
    }
  }

  trait TestFixture {

    class TestController() extends CallbackController

    val controller = new TestController()

    def makeCallbackJsonPostRequest(json: String): FakeRequest[AnyContentAsJson] = FakeRequest(Helpers.POST, "/faststream/callback")
      .withJsonBody(Json.parse(json))

    def makeCallbackPostRequest(content: String): FakeRequest[String] = FakeRequest(Helpers.POST, "/faststream/callback")
      .withBody(content)

    val setupProcessJson =
      s"""
         | {
         | "candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6",
         | "custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9",
         | "interview_id":13917,
         | "custom_interview_id":null,
         | "custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19",
         | "status":"setup_process",
         | "deadline":"2016-10-28"
         | }
       """.stripMargin

    val viewPracticeQuestionJson =
      s"""
         | {
         | "candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6",
         | "custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9",
         | "interview_id":13917,
         | "custom_interview_id":null,
         | "custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19",
         | "status":"view_practice_question",
         | "deadline":"2016-10-28"
         | }
       """.stripMargin

    val questionCallbackJson =
      s"""
         | {
         | "candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6",
         | "custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9",
         | "interview_id":13917,
         | "custom_interview_id":null,
         | "custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19",
         | "status":"question",
         | "deadline":"2016-10-28",
         | "question_number":"2"
         |}
       """.stripMargin

    val finishedCallbackJson =
      s"""
         | {
         | "candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6",
         | "custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9",
         | "interview_id":13917,
         | "custom_interview_id":null,
         | "custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19",
         | "status":"finished",
         | "deadline":"2016-10-28"
         | }
       """.stripMargin

    val finalCallbackJson =
      s"""
         | {
         | "candidate_id":"cnd_222670c903da0bf709660ec3129ccdf6",
         | "custom_candidate_id":"FSCND-7292eea2-57f0-4d25-afc7-fd605c9388f9",
         | "interview_id":13917,
         | "custom_interview_id":null,
         | "custom_invite_id":"FSINV-2d442182-c7f4-4380-9187-8b9d8b852a19",
         | "status":"final",
         | "deadline":"2016-10-28"
         | }
       """.stripMargin

    val unrecogniseableJsonValidStatus =
      s"""
         | {
         |   "status": "final"
         | }
       """.stripMargin

    val unrecogniseableJson =
      s"""
         | {
         |   "key": "foo"
         | }
       """.stripMargin

    val invalidJson =
      s"""
         | Not valid Json
       """.stripMargin
  }
}
