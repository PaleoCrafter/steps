package steps

/**
 * S7Model
 *
 * @author PaleoCrafter
 */
trait S7Model {

  case class CpuInfo(moduleType: String, serialNumber: String, asName: String, copyright: String, moduleName: String)

  case class CpInfo(maxPduLength: Int, maxConnections: Int, maxMpiRate: Int, maxBusRate: Int)

  case class Szl(lengthDr: Int, nDr: Int, dataSize: Int, data: Array[Byte])

  type AreaId = AreaIds.Value

  object AreaIds extends Enumeration {
    val Inputs = Value(0x81)
    val Outputs = Value(0x82)
    val Merkers = Value(0x83)
    val Database = Value(0x84)
    val Counters = Value(0x1C)
    val Timers = Value(0x1D)
  }

  type ConnectionType = ConnectionTypes.Value

  object ConnectionTypes extends Enumeration(1) {
    val PG, OP, S7Basic = Value
  }

  case class ConnectionData(address: String, rack: Int = 0, slot: Int = 2, tpe: ConnectionType = ConnectionTypes.PG)

}
