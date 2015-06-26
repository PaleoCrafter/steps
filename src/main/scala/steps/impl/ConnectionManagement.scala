package steps.impl

import java.io.{DataInputStream, DataOutputStream, IOException}
import java.net.{InetSocketAddress, Socket}

import steps.S7._
import steps._

/**
 * ConnectionManagement
 *
 * @author PaleoCrafter
 */
private[impl] trait ConnectionManagement {
  this: S7Client with PacketSending =>

  protected var socket: Socket = _
  protected var input: DataInputStream = _
  protected var output: DataOutputStream = _
  protected var connected = false

  override def connect(): Unit = {
    if (connected)
      throw ClientAlreadyConnected
    connectTcp()
    connectIso()
    negotiatePduLength()
    connected = true
  }

  private[this] def connectTcp(): Unit = {
    val address = new InetSocketAddress(connectionData.address, IsoPort)
    try {
      socket = new Socket()
      socket.connect(address, 5000)
      socket.setTcpNoDelay(true)
      input = new DataInputStream(socket.getInputStream())
      output = new DataOutputStream(socket.getOutputStream())
    } catch {
      case e: IOException => throw TcpConnectionFailed(e)
    }
  }

  private[this] def connectIso(): Unit = {
    val localTSAP = 0x0100 & 0x0000FFFF
    val remoteTSAP = ((connectionData.tpe.id << 8) + (connectionData.rack * 0x20) + connectionData.slot) & 0x0000FFFF

    val localTSAPHi = (localTSAP >> 8).toByte
    val localTSAPLo = (localTSAP & 0x00FF).toByte
    val remoteTSAPHi = (remoteTSAP >> 8).toByte
    val remoteTSAPLo = (remoteTSAP & 0x00FF).toByte

    DataArrays.ISO_CR(16) = localTSAPHi
    DataArrays.ISO_CR(17) = localTSAPLo
    DataArrays.ISO_CR(20) = remoteTSAPHi
    DataArrays.ISO_CR(21) = remoteTSAPLo

    sendPacket(DataArrays.ISO_CR)
    val size = receiveIsoPacket()
    if (size == 22) {
      if (lastPduType != 0xD0.toByte) throw IsoConnectionRefused()
    } else
      throw IsoPduInvalid()
  }

  private[this] def negotiatePduLength(): Unit = {
    DataArrays.S7_PN.word(23, DefaultPduSizeRequested)
    sendPacket(DataArrays.S7_PN)
    val length = receiveIsoPacket()
    if ((length == 27) && (pdu(17) == 0) && (pdu(18) == 0)) {
      _pduLength = pdu.word(25)
      if (_pduLength == 0) throw IsoPduLengthNegotiationFailed()
    }
    else throw IsoPduLengthNegotiationFailed()
  }

  override def reconnect(): Unit = {
    disconnect()
    connect()
  }

  override def disconnect(): Unit = {
    if (!connected)
      throw ClientNotConnected
    output.close()
    input.close()
    socket.close()
    _pduLength = 0
    connected = false
  }
}
