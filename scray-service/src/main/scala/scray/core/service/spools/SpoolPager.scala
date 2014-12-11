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

package scray.core.service.spools

import scray.core.service._
import com.twitter.util.Future
import scray.service.qservice.thrifscala.ScrayTResultFrame
import scray.service.qmodel.thrifscala.ScrayTRow
import scray.querying.description.Row
import scray.service.qmodel.thrifscala.ScrayTColumnInfo
import scray.common.serialization.KryoPoolSerialization
import java.nio.ByteBuffer
import scray.querying.description.Column
import scray.querying.description.RowColumn
import com.twitter.concurrent.Spool
import scala.annotation.tailrec

class SpoolPager(spool : ServiceSpool) {

  final val DEFAULT_PAGESIZE : Int = 50
  val pagesize : Int = spool.tQueryInfo.pagesize.getOrElse(DEFAULT_PAGESIZE)

  // fetches a page worth of of converted rows and returns it paired with the remaining spool
  def page() : Future[(Seq[ScrayTRow], Spool[Row])] = part(Seq(spool.spool.head), spool.spool.tail, pagesize - 1)
    .map { pair => (pair._1 map { convertRow(_) }, pair._2) }

  // recursive function parting a given spool into an eager head (Seq part) and a lazy tail (Spool part)
  private def part(dest : Seq[Row], src : Future[Spool[Row]], pos : Int) : Future[(Seq[Row], Spool[Row])] =
    if (pos <= 0) src.map { (dest, _) } else src.flatMap { spool => part(dest :+ spool.head, spool.tail, pos - 1) }

  private def convertRow(sRow : Row) : ScrayTRow =
    ScrayTRow(None, sRow.getColumns.map { col => encode(sRow.getColumnValue(col)) })

  private def encode[V](value : V) : ByteBuffer =
    ByteBuffer.wrap(KryoPoolSerialization.chill.toBytesWithClass(value))

}