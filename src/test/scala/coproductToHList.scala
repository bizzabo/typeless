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

import ai.x.typeless.coproduct.CoproductToHList
import org.scalatest.{ FunSuite, Matchers }
import shapeless._

class coproductToHList extends FunSuite with Matchers {

  import CoproductToHList.Ops

  test("can convert list of coproducts to hlist") {

    type A = Int :+: String :+: CNil
    type L = Int :: String :: HNil
    val a: Seq[A] = Seq(Coproduct[A](1), Coproduct[A]("a"))

    assert(a.toHList[L] === Some(1 :: "a" :: HNil))
  }

  test("order doesn't matter when converting list of coproducts to hlist") {

    type A = Int :+: String :+: CNil
    type L = String :: Int :: HNil

    val a: Seq[A] = Seq(Coproduct[A](1), Coproduct[A]("a"))

    assert(a.toHList[L] === Some("a" :: 1 :: HNil))
  }

  test("cannot convert list of coproducts to hlist") {

    type A = Int :+: String :+: CNil

    val a: Seq[A] = Seq(Coproduct[A](1), Coproduct[A]("a"))

    assert(a.toHList[Double :: HNil] === None)
  }

}
