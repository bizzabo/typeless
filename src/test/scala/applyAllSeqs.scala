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
  val featureGenerator1 = (x: String, i: Int) => ("feature1" -> (x.size + i))

  val featureGenerator2 = (x: String, i: Int) => ("feature2" -> i)

  val featureGenerator3 = (x: String, s: Char, i: Int) => ("feature3" -> (s.toInt + i + x.size))

  //is possible to have generators with the same context, they will all be used
  val featureGenerator4 = (x: String, s: Char, i: Int) => ("feature4" -> (s.toInt + i * 2 + x.size))

  object FeatureGenerators {

    val generators1 = Seq(
      featureGenerator1,
      featureGenerator2
    )
    val generators2 = Seq(
      featureGenerator3,
      featureGenerator4
    )
    val generators =
      generators1 ::
        generators2 :: HNil
  }

  val hi = "hi"
  test("single argument") {
    assert(
      SelectFunctionsSeq.runAll(hi)(FeatureGenerators.generators).isEmpty
    )
  }
  test("two arguments") {
    assert(
      SelectFunctionsSeq.runAll(hi, 1)(FeatureGenerators.generators) == Seq("feature1" -> 3, "feature2" -> 1)
    )
  }
  test("three arguments") {
    assert(
      SelectFunctionsSeq.runAll(hi, 1, 2d)(FeatureGenerators.generators) == Seq("feature1" -> 3, "feature2" -> 1)
    )
  }
  test("different three arguments") {
    assert(
      SelectFunctionsSeq.runAll(hi, 'a', 1)(FeatureGenerators.generators) == Seq("feature1" -> 3, "feature2" -> 1, "feature3" -> 100, "feature4" -> 101)
    )
  }
  test("four arguments in different order") {
    // the order of the arguments doesn't matter
    assert(
      SelectFunctionsSeq.runAll(hi, 2d, 1, 'a')(FeatureGenerators.generators) == Seq("feature1" -> 3, "feature2" -> 1, "feature3" -> 100, "feature4" -> 101)
    )
  }

}
