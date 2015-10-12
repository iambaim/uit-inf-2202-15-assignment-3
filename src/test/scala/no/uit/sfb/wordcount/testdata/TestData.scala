package no.uit.sfb.wordcount.testdata

import java.io.InputStream
import java.nio.file.Paths

object TestData {
  def byName(name: String): InputStream = getClass.getResourceAsStream(name)
  def pathByName(name: String): String = {
    val url = getClass.getResource(name)
    Paths.get(url.toURI).toAbsolutePath.toString
  }
  def archiveWarc() = byName("archiveWarc.warc")
  def archiveWarcPath = pathByName("archiveWarc.warc")
}
