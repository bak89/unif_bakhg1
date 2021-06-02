package ch.bfh.ti.unif

object Logger {
  var DebugEnabled = false

  def debug(s: String): Unit = {
    if (DebugEnabled) {
      println(s)
    }
  }
}
