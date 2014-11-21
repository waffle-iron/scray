// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package scray.common.exceptions

import java.util.UUID

object ExceptionIDs {
  // SIL-Scray-Commons-000+ error types
  val GENERAL_FAULT = "SIL-Scray-Commons-001"
}

class ScrayException(id : String, query : Option[UUID], msg : String)
  extends Exception(s"$id: $msg for query ${query}") with Serializable
