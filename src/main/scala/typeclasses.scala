package ai.x
package typeless

import shapeless._
import ops.hlist.SelectAll
import syntax.std.function._
import ops.function._

package object hlist {

  /*takes an HList of functions and an HList of potential arguments
   * it applies the arguments to the functions for which all the arguments are present
   * it return an HList with the results
   */
  trait SelectFunctions[Fs <: HList, Context <: HList] {
    type Out <: HList
    def apply(fs: Fs, context: Context): Out
  }

  trait SelectFunctions0 {
    implicit def hconsNotFound[F, Fs <: HList, Context <: HList, Args <: HList, RT <: HList](
      implicit
      applyContext: SelectFunctions.Aux[Fs, Context, RT]
    ): SelectFunctions.Aux[F :: Fs, Context, RT] = new SelectFunctions[F :: Fs, Context] {
      type Out = RT
      def apply(fs: F :: Fs, context: Context) =
        applyContext(fs.tail, context)
    }

  }
  object SelectFunctions extends SelectFunctions0 {
    type Aux[Fs <: HList, Context <: HList, R <: HList] = SelectFunctions[Fs, Context] { type Out = R }
    implicit def hcons[F, Fs <: HList, Context <: HList, Args <: HList, R, RT <: HList](
      implicit
      fp: FnToProduct.Aux[F, Args => R],
      subset: SelectAll[Context, Args],
      applyContext: SelectFunctions.Aux[Fs, Context, RT]
    ): SelectFunctions.Aux[F :: Fs, Context, R :: RT] = new SelectFunctions[F :: Fs, Context] {
      type Out = R :: RT
      def apply(fs: F :: Fs, context: Context) =
        fs.head.toProduct(subset(context)) :: applyContext(fs.tail, context)
    }

    implicit def hnil[Context <: HList]: SelectFunctions.Aux[HNil, Context, HNil] = new SelectFunctions[HNil, Context] {
      type Out = HNil
      def apply(fs: HNil, context: Context) = HNil
    }

    def runAll[Context <: HList, Fs <: HList, R](context: Context)(fs: Fs)(
      implicit
      selectFunctions: SelectFunctions[Fs, Context]
    ) = selectFunctions(fs, context)

    def runAll[Context <: Product, HContext <: HList, Fs <: HList, R](context: Context)(fs: Fs)(
      implicit
      gen: Generic.Aux[Context, HContext],
      selectFunctions: SelectFunctions[Fs, HContext]
    ) = selectFunctions(fs, gen.to(context))

    def runAll[X, Fs <: HList, R](x: X)(fs: Fs)(
      implicit
      selectFunctions: SelectFunctions[Fs, X :: HNil]
    ) = selectFunctions(fs, x :: HNil)
  }

  /*takes an HList of functions, which all return the same type, and an HList of potential arguments
   * it applies the arguments to the functions for which all the arguments are present
   * it return an Seq with the results
   */
  trait SelectFunctionsSeq[Fs <: HList, Context <: HList] {
    type Out
    def apply(fs: Fs, context: Context): Seq[Out]
  }

  object SelectFunctionsSeq {
    type Aux[Fs <: HList, Context <: HList, R] = SelectFunctionsSeq[Fs, Context] { type Out = R }
    implicit def hcons[Context <: HList, Fs <: HList, F, Args <: HList, R](
      implicit
      fp: FnToProduct.Aux[F, Args => R],
      subset: Subset[Context, Args],
      selectFunctionsTail: SelectFunctionsSeq.Aux[Fs, Context, R]
    ): SelectFunctionsSeq.Aux[F :: Fs, Context, R] = new SelectFunctionsSeq[F :: Fs, Context] {
      type Out = R
      def apply(fs: F :: Fs, context: Context): Seq[Out] = {
        subset(context).map(args => fs.head.toProduct(args)).toSeq ++
          selectFunctionsTail(fs.tail, context)
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

  /*takes an HList of HLists of functions, which all return the same type, and an HList of potential arguments
   * it applies the arguments to the functions for which all the arguments are present
   * it return an Seq with the results
   */
  trait ApplyEachSeq[Context <: HList, Fss <: HList] {
    type Out
    def apply(fs: Fss, args: Context): Seq[Out]
  }

  object ApplyEachSeq {
    type Aux[Context <: HList, Fss <: HList, R] = ApplyEachSeq[Context, Fss] { type Out = R }
    implicit def hcons[Context <: HList, Fs <: HList, Fss <: HList, R](
      implicit
      selectFunctions: SelectFunctionsSeq.Aux[Fs, Context, R],
      applyEach: ApplyEachSeq.Aux[Context, Fss, R]
    ) = new ApplyEachSeq[Context, Fs :: Fss] {
      type Out = R
      def apply(fs: Fs :: Fss, args: Context) =
        selectFunctions(fs.head, args) ++ applyEach(fs.tail, args)
    }
    implicit def hnil[Context <: HList, R] =
      new ApplyEachSeq[Context, HNil] {
        type Out = R
        def apply(fs: HNil, args: Context) = Seq.empty
      }

    def runAll[Context <: Product, HContext <: HList, Fss <: HList](args: Context)(fs: Fss)(
      implicit
      gen: Generic.Aux[Context, HContext],
      applyEach: ApplyEachSeq[HContext, Fss]
    ) = applyEach(fs, gen.to(args))
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
