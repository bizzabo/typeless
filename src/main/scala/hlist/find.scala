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
package hlist

import shapeless._

/*
 * "tries" to find A in L,
 * if A is present it returns Some[A], 
 * otherwise it returns None
 */
trait Find[L <: HList, A] {
  def find(l: L): Option[A]
}

object Find {
  def apply[A, L <: HList](implicit f: Find[L, A]) = f
  implicit class Ops[L <: HList](l: L) {
    def find[A](implicit f: Find[L, A]) = f.find(l)
  }
  implicit def hconsFound[A, H, T <: HList](implicit ev: H =:= A) = new Find[H :: T, A] {
    def find(l: H :: T) = Some(l.head)
  }
  implicit def hconsNotFound[A, H, T <: HList](implicit f: Find[T, A]) = new Find[H :: T, A] {
    def find(l: H :: T) = f.find(l.tail)
  }
  implicit def hnil[A] = new Find[HNil, A] {
    def find(l: HNil) = None
  }
}
