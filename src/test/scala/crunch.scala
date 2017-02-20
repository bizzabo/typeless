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
package test

import org.scalatest._
import shapeless._
import typeless.hlist._

class CrunchTests extends FunSuite with Matchers {

  test("FlattenFunctionsSeq") {
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
    val res: Seq[Int] = FlattenFunctionsSeq.applyAll(1, "a")(functions)
    assert(
      res === Seq(2, 1)
    )
  }

  test("FlattenFunctions") {
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
    val res = FlattenFunctions.applyAll(1, "a")(functions)
    assert(
      res === 2 :: 1.0 :: HNil
    )
  }

  val ls =
    { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      { (i: Int, s: String) => s"$i + $s" } ::
      HNil
  val all =
    ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      ls ::
      HNil

  test("apply all") {
    val res: Seq[String] = FlattenFunctionsSeq.applyAll(1, "a")(all)
    assert(res.distinct === Seq("1 + a"))
  }

  test("apply all Hlist") {
    val res = FlattenFunctions.applyAll(1, "a")(all)
    assert(res.runtimeList.map(_.asInstanceOf[String]).distinct === List("1 + a"))
  }

}

