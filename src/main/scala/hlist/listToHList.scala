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

trait ListToHList[L, H <: HList] {
  def toHList(l: Seq[L]): Option[H]
}

object ListToHList {
  implicit class Ops[L, H <: HList](l: Seq[L]) {
    def toProduct[P <: Product](
      implicit
      generic: Generic.Aux[P, H],
      listToHList: ListToHList[L, H]
    ): Option[P] = listToHList.toHList(l).map(p => generic.from(p))

    def findByType[A](
      implicit
      listToHList: ListToHList[L, A :: HNil]
    ): Option[A] = {
      listToHList.toHList(l).map(_.head)
    }
  }

  implicit def hcons[L, H, T <: HList](
    implicit
    listToHList: ListToHList[L, T],
    typeable: Typeable[H]
  ): ListToHList[L, H :: T] = new ListToHList[L, H :: T] {
    def toHList(l: Seq[L]): Option[H :: T] = {
      for {
        h <- l.flatMap(_.cast[H]).headOption
        tail <- listToHList.toHList(l)
      } yield h :: tail
    }
  }

  implicit def hnil[L] = new ListToHList[L, HNil] {
    def toHList(c: Seq[L]): Option[HNil] = Some(HNil)
  }
}