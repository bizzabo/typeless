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
package xshapeless
package test

import org.scalatest._
import shapeless._

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

    def features[Context <: HList, Fs <: HList, R](context: Context)(fs: Fs)(
      implicit
      applyAll: ApplyAllSeqs.Aux[Fs, Context, R]
    ): Seq[R] = applyAll(fs, context)

    def features[Context <: Product, HContext <: HList, Fs <: HList, R](context: Context)(fs: Fs)(
      implicit
      gen: Generic.Aux[Context, HContext],
      applyAll: ApplyAllSeqs.Aux[Fs, HContext, R]
    ): Seq[R] = applyAll(fs, gen.to(context))

    def features[X, Fs <: HList, R](x: X)(fs: Fs)(
      implicit
      applyAll: ApplyAllSeqs.Aux[Fs, X :: HNil, R]
    ): Seq[R] = applyAll(fs, x :: HNil)

  }

  val hi = "hi"
  test("single argument") {
    assert(
      FeatureGenerators.features(hi)(FeatureGenerators.generators).isEmpty
    )
  }
  test("two arguments") {
    assert(
      FeatureGenerators.features(hi, 1)(FeatureGenerators.generators) == Seq("feature1" -> 3, "feature2" -> 1)
    )
  }
  test("three arguments") {
    assert(
      FeatureGenerators.features(hi, 1, 2d)(FeatureGenerators.generators) == Seq("feature1" -> 3, "feature2" -> 1)
    )
  }
  test("different three arguments") {
    assert(
      FeatureGenerators.features(hi, 'a', 1)(FeatureGenerators.generators) == Seq("feature1" -> 3, "feature2" -> 1, "feature3" -> 100, "feature4" -> 101)
    )
  }
  test("four arguments in different order") {
    // the order of the arguments doesn't matter
    assert(
      FeatureGenerators.features(hi, 2d, 1, 'a')(FeatureGenerators.generators) == Seq("feature1" -> 3, "feature2" -> 1, "feature3" -> 100, "feature4" -> 101)
    )
  }

}
