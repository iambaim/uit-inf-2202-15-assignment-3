package no.uit.sfb.wordcount

import com.beust.jcommander.Parameter
import edu.cmu.lemurproject.{WritableWarcRecord, WarcFileInputFormat}
import org.apache.hadoop.io.LongWritable
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.jsoup.Jsoup

class WordCount(context: => SparkContext) extends Command {

  lazy val sc = context

  override val args = new {
    @Parameter(names=Array("-i", "--input-file"), required = true)
    var inputFile: String = _

    @Parameter(names=Array("-o", "--output-file"), required = true)
    var outputFile: String = _
  }

  override def apply(): Unit = {
    val counts = wordCount(parsedWarcContent)
    val output = counts.map { case (word, count) => s"$word\t$count" }
    output.saveAsTextFile(args.outputFile)
  }

  def wordCount(rdd: RDD[String]): RDD[(String, Int)] = {
    rdd.flatMap { text =>
      text.split("""\W""").map(word => (word, 1))
    }.reduceByKey( (a, b) => a + b)
  }

  def parsedWarcContent: RDD[String] = {
    sc.newAPIHadoopFile[LongWritable, WritableWarcRecord, WarcFileInputFormat](args.inputFile)
      .map { case (id, warcRecord) => warcRecord.getRecord.getContentUTF8 }
  }


  // todo
  def removeHtml(in: String): String = {
    Jsoup.parse(in).body().text()
  }
}
