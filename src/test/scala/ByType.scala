
package ai.x
package typeless
package test

import org.scalatest.{ FunSuite, Matchers }

class ByType extends FunSuite with Matchers {

  import ai.x.typeless.hlist.ByType._

  sealed trait A
  case class B() extends A
  case class C() extends A
  case class D() extends A

  case class E( b: B, c: C )
  case class F( b: B, d: D )

  val listA: List[A] = List( B(), C() )

  test( ".findByType will find instance of type if it is in the beginning of the list" ) {
    assert( listA.findByType[B] === Some( B() ) )
  }

  test( ".findByType will find instance of type if it is at the end of the list" ) {
    assert( listA.findByType[C] === Some( C() ) )
  }

  test( ".findByType won't find instance if it doesn't exist in list" ) {
    assert( listA.findByType[D] === None )
  }

  test( ".filterByType will filter to list of elements with matching type" ) {
    val listB: List[A] = List( B(), B(), C() )

    assert( listB.filterByType[B] === List( B(), B() ) )
  }

  test( ".filterByType will filter to empty list if there is no matching type" ) {
    val listB: List[A] = List( C() )

    assert( listB.filterByType[B] === Nil )
  }

  test( ".filterNotByType will filter to list of elements that don't match the type" ) {
    val listB: List[A] = List( C(), B() )

    assert( listB.filterNotByType[B] === List( C() ) )
  }
  test( ".containsType returns true if passed type that is contained in seq" ) {
    val listB: List[A] = List( C(), B() )

    assert( listB.containsType[B] === true )
  }
  test( ".containsType returns false if passed type that is contained in seq" ) {
    val listB: List[A] = List( C() )

    assert( listB.containsType[B] === false )
  }

}
