package steps

class S7Exception(message: String, cause: Throwable) extends Exception(message, cause)

case object ClientAlreadyConnected
  extends S7Exception("Client is already connected to a device", null)

case object ClientNotConnected
  extends S7Exception("Client is not connected to any device", null)

class TcpException(message: String, cause: Throwable = null)
  extends S7Exception(message, cause)

case class TcpConnectionFailed(cause: Throwable = null)
  extends TcpException("TCP connection failed", cause)

case class TcpDataSendFailed(cause: Throwable = null)
  extends TcpException("Data could not be sent via TCP", cause)

case class TcpDataReceiveFailed(cause: Throwable = null)
  extends TcpException("Data could not be received via TCP", cause)

case class TcpDataTimedOut(cause: Throwable = null)
  extends TcpException("Waited too long to receive data via TCP", cause)

case class TcpConnectionReset(cause: Throwable = null)
  extends TcpException("TCP connection reset by peer", cause)

class IsoException(message: String, cause: Throwable = null)
  extends S7Exception(message, cause)

case class IsoPduInvalid(cause: Throwable = null)
  extends IsoException("Invalid ISO PDU received", cause)

case class IsoConnectionRefused(cause: Throwable = null)
  extends IsoException("ISO connection refused by CPU", cause)

case class IsoPduLengthNegotiationFailed(cause: Throwable = null)
  extends IsoException("Could not negotiate ISO PDU length successfully", cause)

case class S7PduInvalid(cause: Throwable = null)
  extends S7Exception("Invalid S7 PDU recieved", cause)

case class S7ReadFailed(cause: Throwable = null)
  extends S7Exception("Could not read any data from S7 CPU", cause)

case class S7WriteFailed(cause: Throwable = null)
  extends S7Exception("Could not write any data to S7 CPU", cause)

case class S7BufferTooSmall(cause: Throwable = null)
  extends S7Exception("Buffer supplied to function is too small", cause)

case class S7FunctionRefused(cause: Throwable = null)
  extends S7Exception("The supplied function was refused by the S7 CPU", cause)

case class S7FunctionParametersInvalid(cause: Throwable = null)
  extends S7Exception("The parameters supplied to the function were invalid", cause)