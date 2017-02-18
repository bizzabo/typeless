# typeless

This is the home for some to some typeclasses that might not make it to shapeless itself.

1- `Find[L <: HList, A]` which will allow to find a type `A` in an `HList` `L`, if the type is not present it returns `None`, otherwise `Some[A]`

2 - `Subset[L <: HList,S <: HList]`, similar to `Find`, but for a group of elements, if **all** the elements of the  `S` are present in `L` it returns `Some[S]` otherwise `None`

3 - `SelectFunctions[L <: HList, F <: HList]` if `F` is an `HList` of functions, it will evaluate all for which their arguments can be found in `L`, and return the `HList` of the results

4 - `SelectFunctionsSeq[L <: HList, F <: HList]` if `F` is an `HList` of `Seq` of functions, it will evaluate all for which their arguments can be found in `L`, and return a `Seq[R]`

## Getting started

```scala
libraryDependencies += "ai.x" %% "typeless" % "0.1.4"
```

Currently all operations are on `HLists`, and therefore the only useful import is:

```scala
import typeless.hlist._
```
