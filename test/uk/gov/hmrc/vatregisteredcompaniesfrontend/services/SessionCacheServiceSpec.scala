/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.vatregisteredcompaniesfrontend.services

import cats.data.OptionT
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.{Json, OFormat, Reads}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vatregisteredcompaniesfrontend.repo.SessionStore
import uk.gov.hmrc.vatregisteredcompaniesfrontend.utils.BaseSpec

import scala.concurrent.Future
import scala.reflect.ClassTag

class SessionCacheServiceSpec extends BaseSpec with ScalaFutures {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  case class TestData(id: String, vatNumber: Int)
  implicit val testDataFormat: OFormat[TestData] = Json.format[TestData]

  val mockSessionStore: SessionStore = mock[SessionStore]
  val service = new SessionCacheService(mockSessionStore)

  val cacheId = "testCacheId"
  val testKey = "testKey"
  val testData: TestData = TestData("GB123456789", 456789)

  implicit val reads: Reads[TestData] = testDataFormat
  implicit val classTag: ClassTag[TestData] = ClassTag(classOf[TestData])

  "SessionCacheService" should {

    "retrieve data successfully from cache" in {

      when(mockSessionStore.getCache[TestData](eqTo(cacheId), eqTo(testKey))(eqTo(classTag), any(), any(), eqTo(reads)))
        .thenReturn(OptionT(Future.successful(Option(testData))))

      val result = service.get[TestData](cacheId, testKey).futureValue

      result shouldBe Some(testData)
    }

    "return None when data is not in cache" in {

      when(mockSessionStore.getCache[TestData](eqTo(cacheId), eqTo(testKey))(eqTo(classTag), any(), any(), eqTo(reads)))
        .thenReturn(OptionT(Future.successful(None: Option[TestData])))

      val result = service.get[TestData](cacheId, testKey).futureValue

      result shouldBe None
    }

    "store data successfully in cache" in {

      when(mockSessionStore.putCache(eqTo(cacheId), eqTo(testKey), eqTo(testData))(any(), any(), any()))
        .thenReturn(Future.successful(()))

      val result = service.put(cacheId, testKey, testData).futureValue

      result shouldBe true
    }

    "return false when storing data in cache fails" in {

      when(mockSessionStore.putCache(eqTo(cacheId), eqTo(testKey), eqTo(testData))(any(), any(), any()))
        .thenReturn(Future.failed(new RuntimeException("Cache storage failed")))

      val result = service.put(cacheId, testKey, testData).futureValue

      result shouldBe false
    }
    
  }
}
