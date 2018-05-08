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
import labelled._
import ops.record._

trait EqualsIgnoringFields[T] {
  def equalsIgnoringFields( t1: T, t2: T, field: Symbol => Boolean ): Boolean
}

trait LowPriorityEqualsIgnoringFields {
  implicit def catchAll[A] = new EqualsIgnoringFields[A] {
    def equalsIgnoringFields( x: A, y: A, field: Symbol => Boolean ): Boolean = {
      x == y
    }
  }

}

object EqualsIgnoringFields extends LowPriorityEqualsIgnoringFields {

  def apply[A: EqualsIgnoringFields]: EqualsIgnoringFields[A] = implicitly[EqualsIgnoringFields[A]]

  implicit def generic[T, R]( implicit
    gen: LabelledGeneric.Aux[T, R],
                              eqRepr: Lazy[EqualsIgnoringFields[R]] ): EqualsIgnoringFields[T] =
    new EqualsIgnoringFields[T] {
      def equalsIgnoringFields( x: T, y: T, field: Symbol => Boolean ): Boolean =
        eqRepr.value.equalsIgnoringFields( gen.to( x ), gen.to( y ), field )
    }

  implicit val hnil: EqualsIgnoringFields[HNil] = new EqualsIgnoringFields[HNil] {
    def equalsIgnoringFields( x: HNil, y: HNil, field: Symbol => Boolean ): Boolean = true
  }

  implicit def product[K <: Symbol, H, T <: HList]( implicit
    key: Witness.Aux[K],
                                                    eqH: Lazy[EqualsIgnoringFields[H]],
                                                    eqT: Lazy[EqualsIgnoringFields[T]] ): EqualsIgnoringFields[FieldType[K, H] :: T] =
    new EqualsIgnoringFields[FieldType[K, H] :: T] {
      def equalsIgnoringFields( x: FieldType[K, H] :: T, y: FieldType[K, H] :: T, field: Symbol => Boolean ): Boolean = {
        if ( field( key.value ) ) eqT.value.equalsIgnoringFields( x.tail, y.tail, field )
        else eqH.value.equalsIgnoringFields( x.head, y.head, field ) && eqT.value.equalsIgnoringFields( x.tail, y.tail, field )
      }
    }

  implicit val cnil: EqualsIgnoringFields[CNil] = new EqualsIgnoringFields[CNil] {
    def equalsIgnoringFields( x: CNil, y: CNil, field: Symbol => Boolean ): Boolean = true
  }

  implicit def coproduct[H, T <: Coproduct, K <: Symbol]( implicit
    key: Witness.Aux[K],
                                                          eqH: Lazy[EqualsIgnoringFields[H]],
                                                          eqT: Lazy[EqualsIgnoringFields[T]] ): EqualsIgnoringFields[FieldType[K, H] :+: T] =
    new EqualsIgnoringFields[FieldType[K, H] :+: T] {
      def equalsIgnoringFields( x: FieldType[K, H] :+: T, y: FieldType[K, H] :+: T, field: Symbol => Boolean ): Boolean = {
        ( x, y ) match {
          case ( Inl( xh ), Inl( yh ) ) => eqH.value.equalsIgnoringFields( xh, yh, field )
          case ( Inr( xt ), Inr( yt ) ) => eqT.value.equalsIgnoringFields( xt, yt, field )
          case _                        => false
        }
      }
    }

  implicit class Ops[T]( x: T ) {
    def equalsIgnoringFields( field: Symbol => Boolean )( y: T )( implicit eqT: EqualsIgnoringFields[T] ): Boolean = eqT.equalsIgnoringFields( x, y, field )
  }

}

