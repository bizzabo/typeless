package ai.x

import shapeless._
import syntax.std.function._
import ops.function._

package object xshapeless {

  trait SelectFunctions[Fs <: HList, Context <: HList] {
    type Out
    def apply(fs: Fs, context: Context): Seq[Out]
  }

  object SelectFunctions {
    type Aux[Fs <: HList, Context <: HList, R] = SelectFunctions[Fs, Context] { type Out = R }
    implicit def hcons[F, Fs <: HList, Context <: HList, Args <: HList, R](
      implicit
      fp: FnToProduct.Aux[F, Args => R],
      subset: Subset[Context, Args],
      applyContext: SelectFunctions.Aux[Fs, Context, R]
    ): SelectFunctions.Aux[F :: Fs, Context, R] = new SelectFunctions[F :: Fs, Context] {
      type Out = R
      def apply(fs: F :: Fs, context: Context) =
        subset(context).map(args => fs.head.toProduct(args)).toSeq ++
          applyContext(fs.tail, context)
    }

    implicit def hnil[Context <: HList, R]: SelectFunctions.Aux[HNil, Context, R] = new SelectFunctions[HNil, Context] {
      type Out = R
      def apply(fs: HNil, context: Context) = Seq.empty
    }

    def runAll[Context <: HList, Fs <: HList, R](context: Context)(fs: Fs)(
      implicit
      selectFunctions: SelectFunctions.Aux[Fs, Context, R]
    ): Seq[R] = selectFunctions(fs, context)

    def runAll[Context <: Product, HContext <: HList, Fs <: HList, R](context: Context)(fs: Fs)(
      implicit
      gen: Generic.Aux[Context, HContext],
      selectFunctions: SelectFunctions.Aux[Fs, HContext, R]
    ): Seq[R] = selectFunctions(fs, gen.to(context))

    def runAll[X, Fs <: HList, R](x: X)(fs: Fs)(
      implicit
      selectFunctions: SelectFunctions.Aux[Fs, X :: HNil, R]
    ): Seq[R] = selectFunctions(fs, x :: HNil)
  }

  trait SelectFunctionsSeq[Fs <: HList, Context <: HList] {
    type Out
    def apply(fs: Fs, context: Context): Seq[Out]
  }

  object SelectFunctionsSeq {
    type Aux[Fs <: HList, Context <: HList, R] = SelectFunctionsSeq[Fs, Context] { type Out = R }
    implicit def hcons[Context <: HList, Fs <: HList, F, Args <: HList, R, S[Q] <: Seq[Q]](
      implicit
      fp: FnToProduct.Aux[F, Args => R],
      subset: Subset[Context, Args],
      t: SelectFunctionsSeq.Aux[Fs, Context, R]
    ): SelectFunctionsSeq.Aux[S[F] :: Fs, Context, R] = new SelectFunctionsSeq[S[F] :: Fs, Context] {
      type Out = R
      def apply(fs: S[F] :: Fs, context: Context): Seq[Out] = {
        val res = for {
          f <- fs.head
          args <- subset(context)
        } yield f.toProduct(args)
        res ++ t(fs.tail, context)
      }
    }
    implicit def hnil[Context <: HList, R]: SelectFunctionsSeq.Aux[HNil, Context, R] = new SelectFunctionsSeq[HNil, Context] {
      type Out = R
      def apply(fs: HNil, context: Context): Seq[Out] = Seq.empty
    }

    def runAll[Context <: HList, Fs <: HList, R](context: Context)(fs: Fs)(
      implicit
      selectFunctions: SelectFunctionsSeq.Aux[Fs, Context, R]
    ): Seq[R] = selectFunctions(fs, context)

    def runAll[Context <: Product, HContext <: HList, Fs <: HList, R](context: Context)(fs: Fs)(
      implicit
      gen: Generic.Aux[Context, HContext],
      selectFunctions: SelectFunctionsSeq.Aux[Fs, HContext, R]
    ): Seq[R] = selectFunctions(fs, gen.to(context))

    def runAll[X, Fs <: HList, R](x: X)(fs: Fs)(
      implicit
      selectFunctions: SelectFunctionsSeq.Aux[Fs, X :: HNil, R]
    ): Seq[R] = selectFunctions(fs, x :: HNil)

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
