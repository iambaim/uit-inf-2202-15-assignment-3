package no.uit.sfb.wordcount

import com.beust.jcommander.Parameter
import edu.cmu.lemurproject.{WritableWarcRecord, WarcFileInputFormat}
import org.apache.hadoop.io.LongWritable
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.jsoup.Jsoup

/** WordcCount class constructor (using spark) **/

class WordCount(context: => SparkContext) extends Command {

  /** Connect to the Spark cluster: */

  lazy val sc = context

  /** Arguments, one for input and one for output **/

  override val args = new {
    @Parameter(names=Array("-i", "--input-file"), required = true)
    var inputFile: String = _

    @Parameter(names=Array("-o", "--output-file"), required = true)
    var outputFile: String = _
  }

  /** The main function call, connects with the Main.scala **/

  override def apply(): Unit = {

    /** Calling the wordCount method, send the output to counts **/

    val counts = wordCount(parsedWarcContent)

    /** Save the map output as  the "word <tab> count" list. See the output **/

    val output = counts.map { case (word, count) => s"$word\t$count" }
    output.saveAsTextFile(args.outputFile)
  }

  /**
  The main wordCount method, basically it will:

      1) parse the content of the parsed warcs records and split
      each words (using the non-word "\W"
      limiter, e.g., "100% search" will be "100" and "search" and mapped each
      words into a tuple of (word, 1), etc. ).

      2) Reduce the by key of all the records, so if you have "search" mapped
      two times, then in the end it will become a single tuple of (search, 2).

  Take a look at: http://spark.apache.org/docs/latest/quick-start.html for
  a quick start tutorial on map + reduce using spark.
  **/

  def wordCount(rdd: RDD[String]): RDD[(String, Int)] = {
    rdd.flatMap { text =>
      text.split("""\W""").map(word => (word, 1))
    }.reduceByKey( (a, b) => a + b)
  }

  /**
  Method for opening the Warc file and parse the content of
  each one of them as RDD records using  id and warcRecord
  (String and int) as keys and text as its value (in UTF8)
  http://boston.lti.cs.cmu.edu/clueweb09/wiki/tiki-index.php?page=Working+with+WARC+Files
  **/

  def parsedWarcContent: RDD[String] = {
    sc.newAPIHadoopFile[LongWritable, WritableWarcRecord, WarcFileInputFormat](args.inputFile)
      .map { case (id, warcRecord) => warcRecord.getRecord.getContentUTF8 }
  }

  /**
  todo, unimplemented basically the idea was to remove all html notations
  but since we used the non-word limiter \W, it doesn't seem to be necessary
  **/

  def removeHtml(in: String): String = {
    Jsoup.parse(in).body().text()
  }
}
