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

import ai.x.typeless.coproduct.Convert
import org.scalatest._
import shapeless._

class IntersectTests extends FunSuite with Matchers {

  type A = String :+: Double :+: CNil
  type B = Double :+: String :+: List[Int] :+: CNil

  test("convert") {
    assert(
      Convert[A, B].convert(Coproduct[A]("test")) === Some(Coproduct[B]("test"))
    )
  }
  test("don't convert") {
    assert(
      Convert[B, A].convert(Coproduct[B](List(1))) === None
    )
  }
}
