# typeless

Some typeclasses inspired and powered by shapeless

#### `Find[L <: HList, A]`

Will allow to find a type `A` in an `HList` `L`, if the type is not present it returns `None`, otherwise `Some[A]`

#### `Subset[L <: HList,S <: HList]`

Similar to `Find`, but for a group of elements, if **all** the elements of the  `S` are present in `L` it returns `Some[S]` otherwise `None`

#### `SelectFunctions[L <: HList, FF <: HList]`

Takes an `HList` of functions `FF` and an `HList` of potential arguments `Context`. It applies the arguments to the functions for which all the arguments are present. It returns an `HList` with the results

#### example

```scala
val functions =
    { (x: String, i: Int, d: Double) => d.toInt * i } ::
      { (x: String, i: Int) => s"$x + $i" } ::
      { (x: String, s: Char, i: Int) => i.toDouble } :: 
      { (x: String, s: Char, i: Int) => s.toInt + i * 2 + x.size } ::
      { (x: String) => x.size } ::
      { (x: Char) => x.toInt } ::
      HNil

SelectFunctions.runAll(1, hi)(functions) == "hi + 1" :: 2 :: HNil
SelectFunctions.runAll(hi, 1, 2d)(functions) == 2 :: "hi + 1" :: 2 :: HNil
SelectFunctions.runAll(hi, 'a', 1)(functions) == "hi + 1" :: 1.0 :: 101 :: 2 :: 97 :: HNil
```

#### `SelectFunctionsSeq[L <: HList, FF <: HList]`

Takes an `HList` `FF` of functions, which all return the same type `R`, and an `HList` of potential arguments `Context`. It applies the arguments to the functions for which all the arguments are present. It return an `Seq[R]` with the results.

#### example

```scala
val functions =
    { (x: String, i: Int) => ("feature1" -> (x.size + i)) } ::
      { (x: String, i: Int) => ("feature2" -> i) } ::
      { (x: String, s: Char, i: Int) => ("feature3" -> (s.toInt + i + x.size)) } ::
      { (x: String, s: Char, i: Int) => ("feature4" -> (s.toInt + i * 2 + x.size)) } ::
      HNil

SelectFunctionsSeq.runAll(hi)(functions).isEmpty
SelectFunctionsSeq.runAll(hi, 1)(functions) == Seq("feature1" -> 3, "feature2" -> 1)
// an extra argument makes no difference if there are no functions that use it
SelectFunctionsSeq.runAll(hi, 1, 2d)(functions) == Seq("feature1" -> 3, "feature2" -> 1)
 ```


#### `FlattenFunctions[Context <: HList, FFF <: HList]` 

Takes an `HList` of `HLists` of functions and an `HList` of potential arguments, and uses `SelectFunctions[Context, FF]` to calculate the resulting `HList`.

#### example

```scala
 val functions1 =
      { (x: String, i: Int) => (x.size + i) } ::
        { (x: String, s: Char, i: Int) => (s.toInt + i * 2 + x.size) } ::
        HNil
val functions2 =
      { (x: String, s: Char, i: Int) => (s.toInt + i + x.size) } ::
        { (i: Int) => i.toDouble } ::
        HNil

val functions = functions1 ::
      functions2 ::
      HNil

FlattenFunctions.runAll(1, "a")(functions) === 2 :: 1.0 :: HNil
```


#### `FlattenFunctionsSeq[Context <: HList, FFF <: HList]`

Takes an `HList` of `HLists` of functions and an `HList` of potential arguments, and uses `SelectFunctionsSeq[Context, FF]` to calculate `Seq[R]`. Meaning all functions most return the same type `R`.

#### example

```scala
val functions1 =
      { (x: String, i: Int) => (x.size + i) } ::
        { (x: String, s: Char, i: Int) => (s.toInt + i * 2 + x.size) } ::
        HNil
val functions2 =
      { (x: String, s: Char, i: Int) => (s.toInt + i + x.size) } ::
        { (i: Int) => i } ::
        HNil

val functions = functions1 ::
      functions2 ::
      HNil

FlattenFunctionsSeq.runAll(1, "a")(functions) === Seq(2, 1)
```

#### `EqualsIgnoringFields[T]`

It compares two cases classes excluding specific *field names* rather than types.

#### example:
```scala

import typeless.hlist.EqualsIgnoringFields.Ops

sealed trait Monarch

case class Butterflies(
  _id: Long,
  date: Long,
  count: Int
) extends Monarch

case class Dictator(
  _id: Long,
  date: Long,
  count: Int
) extends Monarch

val butterfliesStation1 = Butterflies(
      _id = 1,
      date = 1131,
      count = 3
    )
val butterfliesStation2 = Butterflies(
      _id = 2,
      date = 1131,
      count = 2
    )

// the two objects are the same if we ignore those two fields
assert(butterfliesStation1.equalsIgnoringFields(field => field == '_id || field == 'count)(butterfliesStation2)) 
// the two objects are different if not ignoring `count`
assert(!butterfliesStation1.equalsIgnoringFields(_ == '_id)(butterfliesStation2))
// the two objects are different, period
assert(butterfliesStation1 != butterfliesStation2) 

```

## Getting started

```scala
libraryDependencies += "ai.x" %% "typeless" % "0.2.0"
```

Currently all operations are on `HLists`, and therefore the only useful import is:

```scala
import typeless.hlist._
```
