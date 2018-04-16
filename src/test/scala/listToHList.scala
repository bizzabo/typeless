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

import ai.x.typeless.hlist.ListToHList
import org.scalatest.{ FunSuite, Matchers }
import shapeless.HNil

class listToHList extends FunSuite with Matchers {

  import ListToHList.Ops

  sealed trait A
  case class B() extends A
  case class C() extends A
  case class D() extends A

  case class E(b: B, c: C)
  case class F(b: B, d: D)

  val listA: List[A] = List(B(), C())

  test("can convert a list of values to a product if the values contained within the list match the required types of the product") {
    assert(listA.toProduct[E] === Some(E(B(), C())))
  }

  test("won't convert a list, if the types don't match") {
    assert(listA.toProduct[F] === None)
  }

  test("will find instance of type if it is in the beginning of the list") {
    assert(listA.findByType[B] === Some(B()))
  }

  test("will find instance of type if it is at the end of the list") {
    assert(listA.findByType[C] === Some(C()))
  }

  test("won't find instance if it doesn't exist in list") {
    assert(listA.findByType[D] === None)
  }
}