package ai.x
package typeless

import shapeless._

package object coproduct {
  trait Intersect[L <: Coproduct, +S <:  Coproduct] {
    type Out
    def intersect( l: L ): Option[Out]
  }

  object Intersect {
    def intersect[L <: Coproduct, S <: Coproduct]( implicit f: Intersect[L, S] ) = f
    implicit class Ops[L <: Coproduct](l: L) {
      def intersect[A <: Coproduct](implicit f: Intersect[L, A]):Option[f.Out] = f.intersect(l)
    }
    implicit def cCons[L <: Coproduct, H, T <: Coproduct](
                                                           implicit
                                                           subset: Intersect[L, T],
                                                           selector: shapeless.ops.coproduct.Selector[L,H]
                                                         ) = new Intersect[L, H :+: T] {
      type Out = :+:[H, subset.Out]

      def intersect( l: L ): Option[:+:[H, subset.Out]] = {
        l.select[H].map(Inl(_)).orElse(subset.intersect(l).map(Inr(_))):Option[:+:[H, subset.Out]]
      }
    }

    implicit def cNil[L <: Coproduct]: Intersect[L, Coproduct] = new Intersect[L, CNil] {
      type Out = CNil

      def intersect( l: L ) = None
    }
  }
}
