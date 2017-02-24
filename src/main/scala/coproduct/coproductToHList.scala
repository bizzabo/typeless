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
package coproduct

import shapeless.ops.coproduct.Selector
import shapeless.{ ::, Coproduct, HList, HNil }

trait CoproductToHList[C <: Coproduct, L <: HList] {
  def apply(c: Seq[C]): Option[L]
}

trait CoproductToHList0 {
  implicit def hconsNotFound[H, T <: HList, C <: Coproduct] = new CoproductToHList[C, H :: T] {
    def apply(c: Seq[C]): Option[H :: T] = None
  }
}
object CoproductToHList extends CoproductToHList0 {
  def apply[C <: Coproduct, L <: HList](implicit c: CoproductToHList[C, L]) = c
  implicit class Ops[C <: Coproduct](c: Seq[C]) {
    def toHList[L <: HList](implicit coproductToHList: CoproductToHList[C, L]) = coproductToHList(c)
  }
  implicit def hcons[H, T <: HList, C <: Coproduct](
    implicit
    select: Selector[C, H],
    coproductToHList: CoproductToHList[C, T]
  ): CoproductToHList[C, H :: T] = new CoproductToHList[C, H :: T] {
    def apply(c: Seq[C]): Option[H :: T] = {
      for {
        h <- c.flatMap(_.select[H]).headOption
        tail <- coproductToHList(c)
      } yield h :: tail
    }
  }
  implicit def hnil[C <: Coproduct] = new CoproductToHList[C, HNil] {
    def apply(c: Seq[C]): Option[HNil] = Some(HNil)
  }
}
