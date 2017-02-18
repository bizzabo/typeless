# typeless

This is the home for some to some typeclasses that might not make it to shapeless itself.

1- `Find[L <: HList, A]`: Will allow to find a type `A` in an `HList` `L`, if the type is not present it returns `None`, otherwise `Some[A]`

2 - `Subset[L <: HList,S <: HList]`: Similar to `Find`, but for a group of elements, if **all** the elements of the  `S` are present in `L` it returns `Some[S]` otherwise `None`

3 - `SelectFunctions[L <: HList, FF <: HList]`: Takes an `HList` of functions `FF` and an `HList` of potential arguments `Context`. It applies the arguments to the functions for which all the arguments are present. It returns an `HList` with the results
   
4 - `SelectFunctionsSeq[L <: HList, FF <: HList]`: Takes an `HList` `FF` of functions, which all return the same type `R`, and an `HList` of potential arguments `Context`. It applies the arguments to the functions for which all the arguments are present. It return an `Seq[R]` with the results.

5 - `ApplyEachSeq[Context <: HList, FFF <: HList]`: Takes an `HList` of `HLists` of functions and an `HList` of potential arguments, and uses `SelectFunctionsSeq[Context, FF]` to calculate `Seq[R]`. Meaning all functions most return the same type `R`.

## Getting started

```scala
libraryDependencies += "ai.x" %% "typeless" % "0.1.4"
```

Currently all operations are on `HLists`, and therefore the only useful import is:

```scala
import typeless.hlist._
```
