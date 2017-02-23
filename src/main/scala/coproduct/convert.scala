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

import shapeless._
import ops.hlist.SelectAll
import ops.adjoin.Adjoin
import syntax.std.function._
import ops.function._
import shapeless.ops.coproduct.Inject

trait Convert[L <: Coproduct, S <: Coproduct] {
  def convert(l: L): Option[S]
}

trait Convert0 {
  implicit def cCons0[H, T <: Coproduct, S <: Coproduct](
    implicit
    subset: Convert[T, S]
  ): Convert[:+:[H, T], S] = new Convert[H :+: T, S] {
    def convert(l: H :+: T): Option[S] = l.tail.flatMap(f => subset.convert(f))
  }
}

object Convert extends Convert0 {
  def apply[L <: Coproduct, S <: Coproduct](implicit f: Convert[L, S]) = f
  def convert[L <: Coproduct, S <: Coproduct](implicit f: Convert[L, S]) = f
  implicit class Ops[L <: Coproduct](l: L) {
    def convert[S <: Coproduct](implicit f: Convert[L, S]): Option[S] = f.convert(l)
  }
  implicit def cCons[H, T <: Coproduct, S <: Coproduct](
    implicit
    inject: Inject[S, H],
    subset: Convert[T, S]
  ): Convert[:+:[H, T], S] = new Convert[H :+: T, S] {
    def convert(l: H :+: T): Option[S] = l.head.map(inject(_))
  }
  implicit def cNil[S <: Coproduct]: Convert[CNil, S] = new Convert[CNil, S] {
    def convert(l: CNil): Option[S] = None
  }
}
