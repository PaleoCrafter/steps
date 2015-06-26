package steps.impl

import steps.{S7Client, S7}
import S7._

/**
 * S7ClientImpl
 *
 * @author PaleoCrafter
 */
private[steps] class S7ClientImpl(val connectionData: ConnectionData)
  extends S7Client
  with DataReadWrite
  with ConnectionManagement
  with PacketSending {
  type Inputs = InputsImpl
  type Outputs = OutputsImpl
  type Merkers = MerkersImpl

  lazy val inputs = new InputsImpl
  lazy val outputs = new OutputsImpl
  lazy val merkers = new MerkersImpl

  override def cpuInfo: CpuInfo = {
    val raw = szl(0x1C, 0, 1024).data
    CpuInfo(
      raw.string(172, 32), // Module Type
      raw.string(138, 24), // Serial Number
      raw.string(2, 24), // AS Name
      raw.string(104, 26), // Copyright
      raw.string(36, 24)) // Module Name
  }

  override def cpInfo: CpInfo = {
    val raw = szl(0x0131, 0, 1024).data
    CpInfo(
      raw.short(2), // Max PDU Length
      raw.short(4), // Max Connections
      raw.dInt(6), // Max MPI Rate
      raw.dInt(10)) // Max Bus Rate
  }

  class InputsImpl extends InputsApi {
    def readSection(section: Int): Byte = readArea(AreaIds.Inputs, section, 1)(0)

    def writeSection(section: Int, data: Byte) = writeArea(AreaIds.Inputs, section, Array(data))

    override def apply(id: Int): Map[Int, Boolean] = {
      val section = readSection(id)
      (for {
        i <- 0 until 8
        set = (section & (1 << id)) != 0
      } yield i -> set).toMap
    }

    override def apply(id: Int, subId: Int): Boolean = (readSection(id) & (1 << subId)) != 0

    override def update(id: Int, subId: Int, value: Boolean): Unit = {
      val section = readSection(id)
      val write = (section & (~(1 << subId))) | ((if (value) 1 else 0) << subId)
      writeSection(id, write.toByte)
    }
  }

  class OutputsImpl extends OutputsApi {
    def readSection(section: Int): Byte = readArea(AreaIds.Outputs, section, 1)(0)

    def writeSection(section: Int, data: Byte) = writeArea(AreaIds.Outputs, section, Array(data))

    override def apply(id: Int): Map[Int, Boolean] = {
      val section = readSection(id)
      (for {
        i <- 0 until 8
        set = (section & (1 << id)) != 0
      } yield i -> set).toMap
    }

    override def apply(id: Int, subId: Int): Boolean = (readSection(id) & (1 << subId)) != 0

    override def update(id: Int, subId: Int, value: Boolean): Unit = {
      val section = readSection(id)
      val write = (section & (~(1 << subId))) | ((if (value) 1 else 0) << subId)
      writeSection(id, write.toByte)
    }
  }

  class MerkersImpl extends MerkersApi {
    def readSection(section: Int): Byte = readArea(AreaIds.Merkers, section, 1)(0)

    def writeSection(section: Int, data: Byte) = writeArea(AreaIds.Merkers, section, Array(data))

    override def apply(id: Int): Map[Int, Boolean] = {
      val section = readSection(id)
      (for {
        i <- 0 until 8
        set = (section & (1 << id)) != 0
      } yield i -> set).toMap
    }

    override def apply(id: Int, subId: Int): Boolean = (readSection(id) & (1 << subId)) != 0

    override def update(id: Int, subId: Int, value: Boolean): Unit = {
      val section = readSection(id)
      val write = (section & (~(1 << subId))) | ((if (value) 1 else 0) << subId)
      writeSection(id, write.toByte)
    }
  }

}
