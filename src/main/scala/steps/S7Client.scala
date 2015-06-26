package steps

import S7._

/**
 * S7Client
 *
 * @author PaleoCrafter
 */
trait S7Client {
  type Inputs <: InputsApi
  type Outputs <: OutputsApi
  type Merkers <: MerkersApi

  def connect(): Unit

  def reconnect(): Unit

  def disconnect(): Unit

  def connectionData: ConnectionData

  def inputs: Inputs

  def outputs: Outputs

  def merkers: Merkers

  def cpuInfo: CpuInfo

  def cpInfo: CpInfo

  def readArea(area: AreaId, start: Int, length: Int): Array[Byte]

  def writeArea(area: AreaId, start: Int, data: Array[Byte]): Unit

  def szl(id: Int, index: Int, bufferSize: Int = 1024): Szl

  trait BooleansApi {
    def apply(id: Int): Map[Int, Boolean]

    def apply(id: Int, subId: Int): Boolean

    def update(id: Int, subId: Int, value: Boolean): Unit
  }

  trait InputsApi extends BooleansApi

  trait OutputsApi extends BooleansApi

  trait MerkersApi extends BooleansApi
}
