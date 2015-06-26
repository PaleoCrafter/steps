package steps

/**
 * S7Implicits
 *
 * @author PaleoCrafter
 */
trait S7Implicits {

  implicit class ByteArrayConverters(array: Array[Byte]) {
    def short(pos: Int): Int = {
      val hi = array(pos)
      val lo = array(pos + 1) & 0x00FF
      (hi << 8) + lo
    }

    def word(pos: Int): Int = {
      val hi = array(pos) & 0x00FF
      val lo = array(pos + 1) & 0x00FF
      (hi << 8) + lo
    }

    def dInt(pos: Int): Int = {
      var result = 0
      result = array(pos)
      result <<= 8
      result += (array(pos + 1) & 0x0FF)
      result <<= 8
      result += (array(pos + 2) & 0x0FF)
      result <<= 8
      result += (array(pos + 3) & 0x0FF)
      result
    }

    def dWord(pos: Int): Long = {
      var result = 0L
      result = (array(pos) & 0x0FF).toLong
      result <<= 8
      result += (array(pos + 1) & 0x0FF).toLong
      result <<= 8
      result += (array(pos + 2) & 0x0FF).toLong
      result <<= 8
      result += (array(pos + 3) & 0x0FF).toLong
      result
    }

    def string(pos: Int, length: Int): String = new String(array.slice(pos, pos + length).takeWhile(_ != 0))

    def word(pos: Int, value: Int): Unit = {
      val word = value & 0x0FFFF
      array(pos) = (word >> 8).toByte
      array(pos + 1) = (word & 0x00FF).toByte
    }
  }

}
