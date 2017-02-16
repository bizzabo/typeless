package ai.x

import shapeless._
import syntax.std.function._
import ops.function._

package object xshapeless {

  trait ApplyAll[Fs <: HList, Context <: HList] {
    type Out
    def apply(fs: Fs, context: Context): Seq[Out]
  }

  object ApplyAll {
    type Aux[Fs <: HList, Context <: HList, R] = ApplyAll[Fs, Context] { type Out = R }
    implicit def hcons[F, Fs <: HList, Context <: HList, Args <: HList, R](
      implicit
      fp: FnToProduct.Aux[F, Args => R],
      subset: Subset[Context, Args],
      applyContext: ApplyAll.Aux[Fs, Context, R]
    ): ApplyAll.Aux[F :: Fs, Context, R] = new ApplyAll[F :: Fs, Context] {
      type Out = R
      def apply(fs: F :: Fs, context: Context) =
        subset(context).map(args => fs.head.toProduct(args)).toSeq ++
          applyContext(fs.tail, context).toSeq
    }

    implicit def hnil[Context <: HList, R]: ApplyAll.Aux[HNil, Context, R] = new ApplyAll[HNil, Context] {
      type Out = R
      def apply(fs: HNil, context: Context) = Seq.empty
    }
  }

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
  import Find.Ops

  trait Subset[L <: HList, S <: HList] {
    def apply(l: L): Option[S]
  }

  object Subset {
    def apply[L <: HList, S <: HList](implicit f: Subset[L, S]) = f
    implicit def hcons[L <: HList, H, T <: HList](
      implicit
      find: Find[L, H],
      subset: Lazy[Subset[L, T]]
    ) = new Subset[L, H :: T] {
      def apply(l: L) =
        for {
          h <- l.find[H]
          t <- subset.value(l)
        } yield h :: t
    }

    implicit def hnil[L <: HList]: Subset[L, HNil] = new Subset[L, HNil] {
      def apply(l: L) = Some(HNil)
    }
  }
}
