package no.uit.sfb.wordcount

import edu.cmu.lemurproject.{WarcRecord, WarcFileInputFormat, WritableWarcRecord}
import org.apache.hadoop.io.LongWritable
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object SparkUtils {
  def readWarc(inputFile: String)(implicit sc: SparkContext): RDD[WarcRecord] = {
    sc.newAPIHadoopFile[LongWritable, WritableWarcRecord, WarcFileInputFormat](inputFile)
      .map { case (id, warcRecord) => warcRecord.getRecord}
  }
}
