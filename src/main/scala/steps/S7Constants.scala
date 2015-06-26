package steps

/**
 * S7Constants
 *
 * @author PaleoCrafter
 */
trait S7Constants {
  val IsoPort = 102
  val DefaultPduSizeRequested = 480
  val IsoHSize = 7
  val MinPduSize = 16
  val MaxPduSize = DefaultPduSizeRequested + IsoHSize
  val WLByte: Byte = 0x02
  val WLCounter: Byte = 0x1C
  val WLTimer: Byte = 0x1D
}
