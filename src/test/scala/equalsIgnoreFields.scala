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
package tests

import org.scalatest._
import hlist.EqualsIgnoringFields.Ops

sealed trait Monarch
case class Butterflies(
    _id:   Long,
    date:  Long,
    count: Int ) extends Monarch
case class Dictator(
    _id:   Long,
    date:  Long,
    count: Int ) extends Monarch

class TestEqualsIgnoreFields extends FunSuite with Matchers {
  test( "same class equals" ) {
    val butterfliesStation1 = Butterflies(
      _id   = 1,
      date  = 1131,
      count = 2 )
    val butterfliesStation2 = Butterflies(
      _id   = 2,
      date  = 1131,
      count = 2 )

    assert( butterfliesStation1.equalsIgnoringFields( _ == '_id )( butterfliesStation2 ) )
    assert( butterfliesStation1 != butterfliesStation2 )
  }
  test( "same class equals but same type" ) {
    val butterfliesStation1: Monarch = Butterflies(
      _id   = 1,
      date  = 1131,
      count = 2 )
    val butterfliesStation2 = Butterflies(
      _id   = 2,
      date  = 1131,
      count = 2 )

    assert( butterfliesStation1.equalsIgnoringFields( _ == '_id )( butterfliesStation2 ) )
    assert( butterfliesStation1 != butterfliesStation2 )
  }

  test( "discard several fields" ) {
    val butterfliesStation1 = Butterflies(
      _id   = 1,
      date  = 1131,
      count = 3 )
    val butterfliesStation2 = Butterflies(
      _id   = 2,
      date  = 1131,
      count = 2 )

    assert( butterfliesStation1.equalsIgnoringFields( field => field == '_id || field == 'count )( butterfliesStation2 ) )
    assert( !butterfliesStation1.equalsIgnoringFields( _ == '_id )( butterfliesStation2 ) )
    assert( butterfliesStation1 != butterfliesStation2 )
  }

  test( "two classes are different" ) {
    val butterfliesStation: Monarch = Butterflies(
      _id   = 1,
      date  = 1131,
      count = 2 )
    val dictatorUltra: Monarch = Dictator(
      _id   = 2,
      date  = 1131,
      count = 2 )

    assert( !butterfliesStation.equalsIgnoringFields( _ == '_id )( dictatorUltra ) )
  }

  test( "same class bad field" ) {
    val butterfliesStation1 = Butterflies(
      _id   = 1,
      date  = 1131,
      count = 2 )
    val butterfliesStation2 = Butterflies(
      _id   = 2,
      date  = 1131,
      count = 2 )

    assert( !butterfliesStation1.equalsIgnoringFields( _ == '_id2 )( butterfliesStation2 ) )
    assert( butterfliesStation1 != butterfliesStation2 )
  }
}
