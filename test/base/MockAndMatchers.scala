/*
 * Copyright 2019 HM Revenue & Customs
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

package base

import org.mockito.verification.VerificationMode
import org.mockito.{Matchers, Mockito}
import org.scalatest.mockito.MockitoSugar

trait MocksAndMatchers extends MockitoSugar {

  def never() = Mockito.never()

  def times(wantedNumberOfInvocations: Int) = Mockito.times(wantedNumberOfInvocations)

  def verify[T](mock: T) = Mockito.verify[T](mock)

  def verify[T](mock: T, mode: VerificationMode) = Mockito.verify[T](mock, mode)

  def when[T](methodCall: T) = Mockito.when[T](methodCall)

  def any[T]() = Matchers.any[T]()

  def mockEq[T](value: T) = Matchers.eq[T](value)

  def mockEq(value: Short) = Matchers.eq(value)

  def mockEq(value: Int) = Matchers.eq(value)

  def mockEq(value: Boolean) = Matchers.eq(value)

  def mockEq(value: String) = Matchers.eq(value)

  def mockEq(value: Long) = Matchers.eq(value)
}