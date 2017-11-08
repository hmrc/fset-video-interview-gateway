package uk.gov.hmrc.fsetlaunchpadgateway.controllers

import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ AnyContentAsEmpty, AnyContentAsJson }
import play.api.test.Helpers._
import org.mockito.Matchers.any
import org.mockito.Mockito._
import play.api.test.{ FakeHeaders, FakeRequest, Helpers }
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.FaststreamClient
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects._
import uk.gov.hmrc.fsetlaunchpadgateway.connectors.faststream.exchangeobjects.reviewed.ReviewedCallbackRequest

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

class CallbackControllerSpec extends BaseControllerSpec with OneAppPerSuite {
  "Callback Controller#present" should {
    "correctly parse and reply to Setup Process Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(setupProcessJson))

      status(result) mustBe OK

      verify(mockFaststreamClient, times(1)).setupProcessCallback(any[SetupProcessCallbackRequest]())(any[HeaderCarrier]())
    }

    "correctly parse and reply to View Practice Question Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(viewPracticeQuestionJson))

      status(result) mustBe OK

      verify(mockFaststreamClient, times(1)).viewPracticeQuestionCallback(any[ViewPracticeQuestionCallbackRequest]())(any[HeaderCarrier]())
    }

    "correctly parse and reply to Question Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(questionCallbackJson))

      status(result) mustBe OK

      verify(mockFaststreamClient, times(1)).questionCallback(any[QuestionCallbackRequest]())(any[HeaderCarrier]())
    }

    "correctly parse and reply to Final Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(finalCallbackJson))

      status(result) mustBe OK

      verify(mockFaststreamClient, times(1)).finalCallback(any[FinalCallbackRequest]())(any[HeaderCarrier]())
    }

    "correctly parse and reply to Finished Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(finishedCallbackJson))

      status(result) mustBe OK

      verify(mockFaststreamClient, times(1)).finishedCallback(any[FinishedCallbackRequest]())(any[HeaderCarrier]())
    }

    "correctly parse and reply to one-reviewer Reviewed Callbacks" in new TestFixture {
      val result = controller.present()(makeCallbackJsonPostRequest(oneReviewerCallbackJson))

      status(result) mustBe OK

      verify(mockFaststreamClient, times(1)).reviewedCallback(any[ReviewedCallbackRequest]())(any[HeaderCarrier]())
    }

    // TODO: Could not login to launchpad as a second reviewer when trying to write tests to generate suitable sample JSON
    "correctly parse and reply to two-reviewer Reviewed Callbacks" ignore {
      /* val result = controller.present()(makeCallbackJsonPostRequest(twoReviewerCallbackJson))

      status(result) mustBe OK

      verify(mockFaststreamClient, times(1)).reviewedCallback(any[ReviewedCallbackRequest]())(any[HeaderCarrier]()) */
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

    val mockFaststreamClient = mock[FaststreamClient]

    when(mockFaststreamClient.setupProcessCallback(any())(any())).thenReturn(Future.successful(()))
    when(mockFaststreamClient.viewPracticeQuestionCallback(any())(any())).thenReturn(Future.successful(()))
    when(mockFaststreamClient.questionCallback(any())(any())).thenReturn(Future.successful(()))
    when(mockFaststreamClient.finishedCallback(any())(any())).thenReturn(Future.successful(()))
    when(mockFaststreamClient.finalCallback(any())(any())).thenReturn(Future.successful(()))
    when(mockFaststreamClient.reviewedCallback(any())(any())).thenReturn(Future.successful(()))

    class TestController() extends CallbackController(mockFaststreamClient)

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

    val oneReviewerCallbackJson =
      s"""
         | {
         |    "candidate_id": "cnd_ac832d202c42a8ebd99e0bec3e36b873",
         |    "custom_candidate_id": "FSCND-45b39222-366d-4652-91ce-6ba0bd37ccd0",
         |    "interview_id": 9,
         |    "custom_interview_id": null,
         |    "custom_invite_id": "FSINV-b4a39cdb-70db-40a8-871e-971c4bd24a76",
         |    "status": "reviewed",
         |    "deadline": "2016-11-16",
         |    "reviews": {
         |        "total_average": {
         |            "type": "video_interview",
         |            "score_text": "42%",
         |            "score_value": 42
         |        },
         |        "reviewers": {
         |            "reviewer_1": {
         |                "name": "AN Reviewer",
         |                "email": "a.n.reviewer@reviewer.com",
         |                "comment": null,
         |                "question_1": {
         |                    "text": "This is the text for question 1?",
         |                    "id": 18,
         |                    "review_criteria_1": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                        "type": "numeric",
         |                        "score": null
         |                    },
         |                    "review_criteria_2": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                        "type": "numeric",
         |                        "score": null
         |                    }
         |                },
         |                "question_2": {
         |                    "text": "This is the text for question 2?",
         |                    "id": 21,
         |                    "review_criteria_1": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description   ",
         |                        "type": "numeric",
         |                        "score": "1"
         |                    },
         |                    "review_criteria_2": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                        "type": "numeric",
         |                        "score": "3.5"
         |                    }
         |                },
         |                "question_3": {
         |                    "text": "This is the text for question 3?",
         |                    "id": 24,
         |                    "review_criteria_1": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                        "type": "numeric",
         |                        "score": "3"
         |                    },
         |                    "review_criteria_2": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description  ",
         |                        "type": "numeric",
         |                        "score": "3"
         |                    }
         |                },
         |                "question_4": {
         |                    "text": "This is the text for question 4?",
         |                    "id": 27,
         |                    "review_criteria_1": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description        ",
         |                        "type": "numeric",
         |                        "score": "1"
         |                    },
         |                    "review_criteria_2": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description  ",
         |                        "type": "numeric",
         |                        "score": "4"
         |                    }
         |                },
         |                "question_5": {
         |                    "text": "This is the text for question 5?",
         |                    "id": 30,
         |                    "review_criteria_1": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description      ",
         |                        "type": "numeric",
         |                        "score": "3.5"
         |                    },
         |                    "review_criteria_2": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description  ",
         |                        "type": "numeric",
         |                        "score": "4"
         |                    }
         |                },
         |                "question_6": {
         |                    "text": "This is the text for question 6?",
         |                    "id": 33,
         |                    "review_criteria_1": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description   ",
         |                        "type": "numeric",
         |                        "score": "4"
         |                    },
         |                    "review_criteria_2": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description     ",
         |                        "type": "numeric",
         |                        "score": "3"
         |                    }
         |                },
         |                "question_7": {
         |                    "text": "This is the text for question 7?",
         |                    "id": 36,
         |                    "review_criteria_1": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                        "type": "numeric",
         |                        "score": "3.5"
         |                    },
         |                    "review_criteria_2": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description  ",
         |                        "type": "numeric",
         |                        "score": "4"
         |                    }
         |                },
         |                "question_8": {
         |                    "text": "This is the text for question 8?",
         |                    "id": 39,
         |                    "review_criteria_1": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description  ",
         |                        "type": "numeric",
         |                        "score": "4"
         |                    },
         |                    "review_criteria_2": {
         |                        "text": "Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description      ",
         |                        "type": "numeric",
         |                        "score": "2.5"
         |                    }
         |                }
         |            }
         |        }
         |    }
         |}
       """.stripMargin

    val twoReviewerCallbackJson =
      s"""
         | {
         |   "candidate_id":"cnd_f7f4577871c300abf42b36e65e878b5f",
         |   "custom_candidate_id":"FSCND-7e0dbbb4-cbea-4d5d-bad4-7dea087e0590",
         |   "interview_id":9,
         |   "custom_interview_id":null,
         |   "custom_invite_id":"FSINV-38cd5261-ba58-4e55-8f4e-21c0a294203f",
         |   "status":"reviewed",
         |   "deadline":"2016-10-30",
         |   "reviews":{
         |      "total_average":{
         |         "type":"video_interview",
         |         "score_text":"35%",
         |         "score_value":35
         |      },
         |      "reviewers":{
         |         "reviewer_1":{
         |            "name":"AN Reviewer",
         |            "email":"a.n.reviewer@reviewers.com",
         |            "comment":"This is a test set of comments",
         |            "question_1":{
         |               "text":"This is the text of question 1",
         |               "id":18,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"3"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"4"
         |               }
         |            },
         |            "question_2":{
         |               "text":"This is the text of question 2",
         |               "id":21,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description   ",
         |                  "type":"numeric",
         |                  "score":"3"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"3"
         |               }
         |            },
         |            "question_3":{
         |               "text":"This is the text of question 3",
         |               "id":24,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"2.5"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"3.5"
         |               }
         |            },
         |            "question_4":{
         |               "text":"This is the text of question 4",
         |               "id":27,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description        ",
         |                  "type":"numeric",
         |                  "score":"2"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"1.5"
         |               }
         |            },
         |            "question_5":{
         |               "text":"This is the text of question 5",
         |               "id":30,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"2"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"2"
         |               }
         |            },
         |            "question_6":{
         |               "text":"This is the text of question 6",
         |               "id":33,
         |               "review_criteria_1":{
         |                  "text":"This is the text of question 6",
         |                  "type":"numeric",
         |                  "score":"4"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description     ",
         |                  "type":"numeric",
         |                  "score":"4"
         |               }
         |            },
         |            "question_7":{
         |               "text":"This is the text of question 7",
         |               "id":36,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"3.5"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description   ",
         |                  "type":"numeric",
         |                  "score":"1"
         |               }
         |            },
         |            "question_8":{
         |               "text":"This is the text of question 8",
         |               "id":39,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"3"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description     ",
         |                  "type":"numeric",
         |                  "score":"2.5"
         |               }
         |            }
         |         },
         |         "reviewer_2":{
         |            "name":"Some other reviewer",
         |            "email":"a.b.reviewer@reviewers.com",
         |            "comment":"This is awesome!",
         |            "question_1":{
         |               "text":"This is the text of question 1",
         |               "id":18,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"2.5"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"3.5"
         |               }
         |            },
         |            "question_2":{
         |               "text":"This is the text of question 2",
         |               "id":21,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description   ",
         |                  "type":"numeric",
         |                  "score":"1.5"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"3.5"
         |               }
         |            },
         |            "question_3":{
         |               "text":"This is the text of question 3",
         |               "id":24,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"3"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"2"
         |               }
         |            },
         |            "question_4":{
         |               "text":"This is the text of question 4",
         |               "id":27,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description        ",
         |                  "type":"numeric",
         |                  "score":"2.5"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"1.5"
         |               }
         |            },
         |            "question_5":{
         |               "text":"This is the text of question 5",
         |               "id":30,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"2.5"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"3"
         |               }
         |            },
         |            "question_6":{
         |               "text":"This is the text of question 6",
         |               "id":33,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description",
         |                  "type":"numeric",
         |                  "score":"2.5"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description     ",
         |                  "type":"numeric",
         |                  "score":"3.5"
         |               }
         |            },
         |            "question_7":{
         |               "text":"This is the text of question 7",
         |               "id":36,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"2.5"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description   ",
         |                  "type":"numeric",
         |                  "score":"3.5"
         |               }
         |            },
         |            "question_8":{
         |               "text":"This is the text of question 8",
         |               "id":39,
         |               "review_criteria_1":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description ",
         |                  "type":"numeric",
         |                  "score":"2"
         |               },
         |               "review_criteria_2":{
         |                  "text":"Criteria \\r\\n 1 and Criteria \\r\\n\\r\\n2 description     ",
         |                  "type":"numeric",
         |                  "score":"3.5"
         |               }
         |            }
         |         }
         |      }
         |   }
         |}
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
