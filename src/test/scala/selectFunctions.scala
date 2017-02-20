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

class SelectFunctionsTests extends FunSuite with Matchers {
  val functions =
    { (x: String, i: Int, d: Double) => d.toInt * i } ::
      { (x: String, i: Int) => s"$x + $i" } ::
      { (x: String, s: Char, i: Int) => i.toDouble } :: //is possible to have functions with the same context, they will all be used
      { (x: String, s: Char, i: Int) => s.toInt + i * 2 + x.size } ::
      { (x: String) => x.size } ::
      { (x: Char) => x.toInt } ::
      HNil

  val hi = "hi"
  test("single argument") {
    assert(
      SelectFunctions.applyAll(hi)(functions) === 2 :: HNil
    )
  }
  test("two arguments") {
    assert(
      SelectFunctions.applyAll(1, hi)(functions) === "hi + 1" :: 2 :: HNil

    )
  }
  test("three arguments") {
    assert(
      SelectFunctions.applyAll(hi, 1, 2d)(functions) === 2 :: "hi + 1" :: 2 :: HNil
    )
  }
  test("different three arguments") {
    assert(
      SelectFunctions.applyAll(hi, 'a', 1)(functions) === "hi + 1" :: 1.0 :: 101 :: 2 :: 97 :: HNil
    )
  }

  test("four arguments in different order") {
    // the order of the arguments doesn't matter
    assert(
      SelectFunctions.applyAll(hi, 2d, 1, 'a')(functions) === 2 :: "hi + 1" :: 1.0 :: 101 :: 2 :: 97 :: HNil
    )
  }

}
