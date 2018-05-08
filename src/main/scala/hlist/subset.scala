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
import ops.hlist.SelectAll
import ops.adjoin.Adjoin
import syntax.std.function._
import ops.function._
import shapeless.ops.coproduct.Inject

import Find.Ops

/*
   * "tries" to find all elements of S in L,
   * if all elements are  present it returns Some[S],
   * otherwise it returns None
   */
trait Subset[L <: HList, S <: HList] {
  def apply( l: L ): Option[S]
}

object Subset {
  def apply[L <: HList, S <: HList]( implicit f: Subset[L, S] ) = f
  implicit def hcons[L <: HList, H, T <: HList](
    implicit
    find:   Find[L, H],
    subset: Lazy[Subset[L, T]] ) = new Subset[L, H :: T] {
    def apply( l: L ) =
      for {
        h <- l.find[H]
        t <- subset.value( l )
      } yield h :: t
  }

  implicit def hnil[L <: HList]: Subset[L, HNil] = new Subset[L, HNil] {
    def apply( l: L ) = Some( HNil )
  }
}

