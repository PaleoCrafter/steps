package steps.impl

import steps.S7._
import steps._

/**
 * DataReadWrite
 *
 * @author PaleoCrafter
 */
private[impl] trait DataReadWrite {
  this: S7Client with ConnectionManagement with PacketSending =>
  override def readArea(area: AreaId, start: Int, length: Int): Array[Byte] = {
    val data = Array.ofDim[Byte](length)
    var totalElements = length
    var offset = 0
    val wordSize = if (area == AreaIds.Counters || area == AreaIds.Timers) 2 else 1
    var s = start
    val maxElements = (_pduLength - 18) / wordSize
    while (totalElements > 0) {
      val elements = if (totalElements > maxElements) maxElements else totalElements
      val requestedSize = elements * wordSize
      System.arraycopy(DataArrays.S7_RW, 0, pdu, 0, DataArrays.Size_RD)
      pdu(27) = area.id.toByte
      var address = if (area == AreaIds.Counters || area == AreaIds.Timers) s else s << 3
      pdu(22) = if (area == AreaIds.Counters) WLCounter else if (area == AreaIds.Timers) WLTimer else pdu(22)
      pdu.word(23, elements)
      pdu(30) = (address & 0x0FF).toByte
      address = address >> 8
      pdu(29) = (address & 0x0FF).toByte
      address = address >> 8
      pdu(28) = (address & 0x0FF).toByte
      sendPacket(pdu, DataArrays.Size_RD)
      val length = receiveIsoPacket()
      if (length >= 25) {
        if (length - 25 == requestedSize && pdu(21) == 0xFF.toByte) {
          System.arraycopy(pdu, 25, data, offset, requestedSize)
          offset += requestedSize
        }
        else throw S7ReadFailed()
      }
      else throw S7PduInvalid()
      totalElements -= elements
      s += elements * wordSize
    }
    data
  }

  override def writeArea(area: AreaId, start: Int, data: Array[Byte]): Unit = {
    var IsoSize: Int = 0
    var offset: Int = 0
    var s = start
    val wordSize = if (area == AreaIds.Counters || area == AreaIds.Timers) 2 else 1
    val maxElements = (_pduLength - 35) / wordSize
    var totalElements = data.length
    while (totalElements > 0) {
      val numElements = if (totalElements > maxElements) maxElements else totalElements
      val dataSize = numElements * wordSize
      IsoSize = DataArrays.Size_WR + dataSize
      System.arraycopy(DataArrays.S7_RW, 0, pdu, 0, DataArrays.Size_WR)
      pdu.word(2, IsoSize)
      var length = dataSize + 4
      pdu.word(15, length)
      pdu(17) = 0x05.toByte
      pdu(27) = area.id.toByte
      var address = if (area == AreaIds.Counters || area == AreaIds.Timers) s else s << 3
      length = if (area == AreaIds.Counters || area == AreaIds.Timers) dataSize else dataSize << 3
      pdu(22) = if (area == AreaIds.Counters) WLCounter else if (area == AreaIds.Timers) WLTimer else pdu(22)
      pdu.word(23, numElements)
      pdu(30) = (address & 0x0FF).toByte
      address = address >> 8
      pdu(29) = (address & 0x0FF).toByte
      address = address >> 8
      pdu(28) = (address & 0x0FF).toByte
      pdu.word(33, length)
      System.arraycopy(data, offset, pdu, 35, dataSize)
      sendPacket(pdu, IsoSize)
      length = receiveIsoPacket()
      if (length == 22) {
        if (pdu.word(17) != 0 || pdu(21) != 0xFF.toByte) S7WriteFailed()
      }
      else S7PduInvalid()

      offset += dataSize
      totalElements -= numElements
      s += numElements * wordSize
    }
  }

  override def szl(id: Int, index: Int, bufferSize: Int): Szl = {
    val buffer = Array.ofDim[Byte](bufferSize)
    var length = 0
    var currentSize = 0
    var offset = 0
    var done = false
    var first = true
    var seqIn: Byte = 0x00
    var seqOut = 0x0000
    var lengthDr = 0
    var nDr = 0
    var dataSize = 0
    do {
      seqOut += 1
      if (first) {
        DataArrays.S7_SZL_FIRST.word(11, seqOut)
        DataArrays.S7_SZL_FIRST.word(29, id)
        DataArrays.S7_SZL_FIRST.word(31, index)
        sendPacket(DataArrays.S7_SZL_FIRST)
      }
      else {
        DataArrays.S7_SZL_NEXT.word(11, seqOut)
        pdu(24) = seqIn
        sendPacket(DataArrays.S7_SZL_NEXT)
      }
      length = receiveIsoPacket()
      if (first) {
        if (length > 32) {
          if (pdu.word(27) == 0 && pdu(29) == 0xFF.toByte) {
            currentSize = pdu.word(31) - 8
            done = pdu(26) == 0x00
            seqIn = pdu(24)
            lengthDr = pdu.word(37)
            nDr = pdu.word(39)
            System.arraycopy(pdu, 41, buffer, offset, currentSize)
            offset += currentSize
            dataSize += currentSize
          }
          else throw S7FunctionRefused()
        }
        else throw S7PduInvalid()
      }
      else {
        if (length > 32) {
          if (pdu.word(27) == 0 && pdu(29) == 0xFF.toByte) {
            currentSize = pdu.word(31)
            done = pdu(26) == 0x00
            seqIn = pdu(24)
            System.arraycopy(pdu, 37, buffer, offset, currentSize)
            offset += currentSize
            dataSize += currentSize
          }
          else throw S7FunctionRefused()
        }
        else throw S7PduInvalid()
      }
      first = false
    } while (!done)
    Szl(lengthDr, nDr, dataSize, buffer)
  }
}
