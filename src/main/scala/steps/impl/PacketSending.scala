package steps.impl

import java.io.IOException

import steps.S7._
import steps._

/**
 * PacketSending
 *
 * @author PaleoCrafter
 */
private[impl] trait PacketSending {
  this: S7Client with ConnectionManagement =>
  protected var lastPduType: Byte = 0
  protected val pdu = Array.ofDim[Byte](2048)
  protected var _pduLength = 0

  private[this] def waitForData(size: Int, timeout: Int = 2000): Unit = {
    var cnt = 0
    var sizeAvailable = 0
    var expired = false
    try {
      sizeAvailable = input.available
      while ((sizeAvailable < size) && (!expired)) {
        cnt += 1
        try
          Thread.sleep(1)
        catch {
          case ex: InterruptedException => throw TcpDataTimedOut(ex)
        }
        sizeAvailable = input.available
        expired = cnt > timeout
        if (expired && (sizeAvailable > 0)) input.read(pdu, 0, sizeAvailable)
      }
    }
    catch {
      case ex: IOException => throw TcpDataTimedOut(ex)
    }
    if (cnt >= timeout)
      throw TcpDataTimedOut(null)
  }

  protected def sendPacket(payload: Array[Byte]): Unit = sendPacket(payload, payload.length)

  protected def sendPacket(payload: Array[Byte], length: Int): Unit = {
    try {
      output.write(payload, 0, length)
      output.flush()
    }
    catch {
      case ex: IOException => throw TcpDataSendFailed(ex)
    }
  }

  protected def receivePacket(buffer: Array[Byte], start: Int, size: Int): Unit = {
    waitForData(size)
    try {
      if (input.read(buffer, start, size) == 0)
        throw TcpConnectionReset()
    }
    catch {
      case ex: IOException => TcpDataReceiveFailed(ex)
    }
  }

  protected def receiveIsoPacket(): Int = {
    var done = false
    var size = 0
    while (!done) {
      receivePacket(pdu, 0, 4)
      size = pdu.word(2)
      if (size == IsoHSize) receivePacket(pdu, 4, 3)
      else {
        if ((size > MaxPduSize) || (size < MinPduSize)) throw IsoPduInvalid()
        else done = true
      }
    }
    receivePacket(pdu, 4, 3)
    lastPduType = pdu(5)
    receivePacket(pdu, 7, size - IsoHSize)
    size
  }
}
