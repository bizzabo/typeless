/*
 * Copyright (c) 2015-6 x.ai inc.
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

package ai.x
package typeless
package test

import org.scalatest._
import shapeless._
import typeless.hlist._

class ApplyAllSeqsTests extends FunSuite with Matchers {
  val functions =
    { (x: String, i: Int) => ("feature1" -> (x.size + i)) } ::
      { (x: String, i: Int) => ("feature2" -> i) } ::
      { (x: String, s: Char, i: Int) => ("feature3" -> (s.toInt + i + x.size)) } ::
      { (x: String, s: Char, i: Int) => ("feature4" -> (s.toInt + i * 2 + x.size)) } ::
      HNil

  val hi = "hi"
  test("single argument") {
    assert(
      SelectFunctionsSeq.applyAll(hi)(functions).isEmpty
    )
  }
  test("two arguments") {
    assert(
      SelectFunctionsSeq.applyAll(hi, 1)(functions) === Seq("feature1" -> 3, "feature2" -> 1)
    )
  }
  test("three arguments") {
    assert(
      SelectFunctionsSeq.applyAll(hi, 1, 2d)(functions) === Seq("feature1" -> 3, "feature2" -> 1)
    )
  }
  test("different three arguments") {
    assert(
      SelectFunctionsSeq.applyAll(hi, 'a', 1)(functions) === Seq("feature1" -> 3, "feature2" -> 1, "feature3" -> 100, "feature4" -> 101)
    )
  }
  test("four arguments in different order") {
    // the order of the arguments doesn't matter
    assert(
      SelectFunctionsSeq.applyAll(hi, 2d, 1, 'a')(functions) === Seq("feature1" -> 3, "feature2" -> 1, "feature3" -> 100, "feature4" -> 101)
    )
  }
  test("can call with hlist") {
    assert(
      SelectFunctionsSeq.applyAll(hi :: 1 :: HNil)(functions) == Seq("feature1" -> 3, "feature2" -> 1)
    )
  }

}
