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

class AncesstorsTests extends FunSuite with Matchers {

  case class OutputTimeEntities(d: Double)
  case class OutputEmail(s: String)
  case class OutputSomethingElse(i: Int)

  //implicit not found error
  trait Ancesstors[A] {
    type Out <: HList
  }
  object Ancesstors {
    type Aux[A, O] = Ancesstors[A] { type Out = O }
    def apply[A](implicit a: Ancesstors[A]) = a
    implicit object timeEntities extends Ancesstors[OutputTimeEntities] {
      type Out = OutputEmail :: OutputSomethingElse :: HNil
    }
    implicit object somethingElse extends Ancesstors[OutputSomethingElse] {
      type Out = OutputEmail :: HNil
    }
    implicit object email extends Ancesstors[OutputEmail] {
      type Out = HNil
    }
  }

  implicit class Ops[Context <: Product, HContext <: HList, Fss <: HList, R, ExpectedContext <: HList](fs: Fss) {

    def features[Stage](args: Context)(
      implicit
      gen: Generic.Aux[Context, HContext],
      distinct: IsDistinctConstraint[HContext],
      ancesstors: Ancesstors.Aux[Stage, ExpectedContext],
      basis: BasisConstraint[ExpectedContext, HContext],
      flattenFunctions: FlattenFunctionsSeq.Aux[HContext, Fss, R]
    ): Seq[R] = flattenFunctions(fs, gen.to(args))
  }

  test("FlattenFunctionsSeq") {
    val functions1 =
      { (x: String, i: Int) => (x.size + i) } ::
        { (x: String, s: Char, i: Int) => (s.toInt + i * 2 + x.size) } ::
        HNil
    val functions2 =
      { (x: String, s: Char, i: Int) => (s.toInt + i + x.size) } ::
        { (i: Int) => i } ::
        HNil

    val functions = functions1 ::
      functions2 ::
      HNil

    val res: Seq[Int] = functions.features[OutputTimeEntities](OutputSomethingElse(1), OutputEmail("a"))
    //val res: Seq[Int] = functions.features[OutputTimeEntities](OutputSomethingElse(1)) <-- won't compile
    def parseContext[Stage](js: JsValue)(
      implicit
      ancesstors: Ancesstors.Aux[Stage, ExpectedContext]
    ): JsResult[ExpectedContext] = js.validate[ExpectedContext]

    val contextJs: Future[JsValue] = Graph.fetchContext[OutputTimeEntities]
    val context = contextJs.map(parseContext[OutputTimeEntities])
    context.map {
      c =>
        functions.features[OutputTimeEntities](c)
    }

    assert(
      res === Seq(2, 1)
    )
  }
}

