package no.uit.sfb.wordcount

import edu.cmu.lemurproject.{WarcRecord, WarcFileInputFormat, WritableWarcRecord}
import org.apache.hadoop.io.LongWritable
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
Example method for opening the Warc file and parse the content of
each one of them as a RDD record using  id and warcRecord
(String and int) as keys and text as its value
http://boston.lti.cs.cmu.edu/clueweb09/wiki/tiki-index.php?page=Working+with+WARC+Files
**/

object SparkUtils {
  def readWarc(inputFile: String)(implicit sc: SparkContext): RDD[WarcRecord] = {
    sc.newAPIHadoopFile[LongWritable, WritableWarcRecord, WarcFileInputFormat](inputFile)
      .map { case (id, warcRecord) => warcRecord.getRecord}
  }
}
