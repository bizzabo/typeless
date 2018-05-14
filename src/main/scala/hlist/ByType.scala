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
package hlist

import shapeless._
import syntax.typeable._

object ByType {

  implicit class ByType[L]( l: Seq[L] ) {
    /** Finds first element in Seq that can be cast to the type of the type parameter
     */

    def findByType[A](
      implicit
      typeable: Typeable[A]
    ): Option[A] = l.filterByType.headOption

    /** Filters to Seq of elements that can be cast to the type of the type parameter
     */

    def filterByType[A](
      implicit
      typeable: Typeable[A]
    ): Seq[A] = l.flatMap( _.cast[A] )

    /** Filters to Seq of elements that cannot be cast to the type of the type parameter
     */

    def filterNotByType[A](
      implicit
      typeable: Typeable[A]
    ): Seq[L] = l.filter { _.cast[A].isEmpty }

    /** Returns true if seq contains an instance of the type paramter
     */

    def containsType[A](
      implicit
      typeable: Typeable[A]
    ): Boolean = l.findByType[A].isDefined
  }
}
