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
  val featureGenerator1 = (x: String, i: Int, d: Double) => d.toInt * i

  val featureGenerator2 = (x: String, i: Int) => s"$x + $i"

  val featureGenerator3 = (x: String, s: Char, i: Int) => i.toDouble

  //is possible to have generators with the same context, they will all be used
  val featureGenerator3_1 = (x: String, s: Char, i: Int) => s.toInt + i * 2 + x.size

  val featureGenerator4 = (x: String) => "string_size" -> x.size

  object FeatureGenerators {

    val generators =
      featureGenerator1 ::
        featureGenerator2 ::
        featureGenerator3 ::
        featureGenerator3_1 ::
        featureGenerator4 :: HNil
  }

  val hi = "hi"
  test("single argument") {
    assert(
      SelectFunctions.runAll(hi)(FeatureGenerators.generators) == "string_size" -> 2 :: HNil
    )
  }
  test("two arguments") {
    assert(
      SelectFunctions.runAll(hi, 1)(FeatureGenerators.generators) == "hi + 1" :: "string_size" -> 2 :: HNil
    )
  }
  test("three arguments") {
    assert(
      SelectFunctions.runAll(hi, 1, 2d)(FeatureGenerators.generators) == 2 :: "hi + 1" :: "string_size" -> 2 :: HNil
    )
  }
  test("different three arguments") {
    assert(
      SelectFunctions.runAll(hi, 'a', 1)(FeatureGenerators.generators) == "hi + 1" :: 1.0 :: 101 :: "string_size" -> 2 :: HNil
    )
  }

  test("four arguments in different order") {
    // the order of the arguments doesn't matter
    assert(
      SelectFunctions.runAll(hi, 2d, 1, 'a')(FeatureGenerators.generators) === 2 :: "hi + 1" :: 1.0 :: 101 :: "string_size" -> 2 :: HNil
    )
  }

}
