package ai.x
package typeless

import shapeless._
import shapeless.ops.coproduct.Inject

package object coproduct {
  trait Intersect[L <: Coproduct, S <: Coproduct] {
    def intersect(l: L): Option[S]
  }

  trait Intersect0 {
    implicit def cCons0[H, T <: Coproduct, S <: Coproduct](
      implicit
      subset: Intersect[T, S]
    ): Intersect[:+:[H, T], S] = new Intersect[H :+: T, S] {
      def intersect(l: H :+: T): Option[S] = l.tail.flatMap(f => subset.intersect(f))
    }
  }

  object Intersect extends Intersect0 {
    def apply[L <: Coproduct, S <: Coproduct](implicit f: Intersect[L, S]) = f
    def intersect[L <: Coproduct, S <: Coproduct](implicit f: Intersect[L, S]) = f
    implicit class Ops[L <: Coproduct](l: L) {
      def intersect[S <: Coproduct](implicit f: Intersect[L, S]): Option[S] = f.intersect(l)
    }
    implicit def cCons[H, T <: Coproduct, S <: Coproduct](
      implicit
      inject: Inject[S, H],
      subset: Intersect[T, S]
    ): Intersect[:+:[H, T], S] = new Intersect[H :+: T, S] {
      def intersect(l: H :+: T): Option[S] = l.head.map(inject(_))
    }
    implicit def cNil[S <: Coproduct]: Intersect[CNil, S] = new Intersect[CNil, S] {
      def intersect(l: CNil): Option[S] = None
    }
  }
}
