package steps

import steps.impl.S7ClientImpl

object S7
  extends S7Constants
  with S7Model
  with S7Implicits {
  def client(ip: String, rack: Int = 0, slot: Int = 2, tpe: ConnectionType = ConnectionTypes.PG): S7Client =
    new S7ClientImpl(ConnectionData(ip, rack, slot, tpe))
}