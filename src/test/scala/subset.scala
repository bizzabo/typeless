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

class SubsetTests extends FunSuite with Matchers {

  val ls = 1 :: 2d :: 'a' :: HNil
  test("find subset") {
    val subset = Subset[Int :: Double :: Char :: HNil, Char :: Int :: HNil]
    assert(
      subset(ls) === Some('a' :: 1 :: HNil)
    )
  }
  test("not find a subset") {
    val subset = Subset[Int :: Double :: Char :: HNil, String :: Char :: HNil]
    assert(
      subset(ls) === None
    )
  }

}
