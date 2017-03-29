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
import syntax.typeable._

trait ListToProduct[L, H <: HList] {
  def toProduct(l: Seq[L]): Option[H]
}

object ListToProduct {
  implicit class Ops[L, H <: HList](l: List[L]) {
    def toProduct[P <: Product](
      implicit
      generic: Generic.Aux[P, H],
      listToProduct: ListToProduct[L, H]
    ): Option[P] = listToProduct.toProduct(l).map(p => generic.from(p))
  }

  implicit def hcons[L, H, T <: HList](
    implicit
    listToProduct: ListToProduct[L, T],
    typeable: Typeable[H]
  ): ListToProduct[L, H :: T] = new ListToProduct[L, H :: T] {
    def toProduct(l: Seq[L]): Option[H :: T] = {
      for {
        h <- l.flatMap(_.cast[H]).headOption
        tail <- listToProduct.toProduct(l)
      } yield h :: tail
    }
  }
  implicit def hnil[L] = new ListToProduct[L, HNil] {
    def toProduct(c: Seq[L]): Option[HNil] = Some(HNil)
  }
}