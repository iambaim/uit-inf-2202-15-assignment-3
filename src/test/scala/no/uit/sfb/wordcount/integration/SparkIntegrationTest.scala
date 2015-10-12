package no.uit.sfb.wordcount.integration

import edu.cmu.lemurproject._
import no.uit.sfb.wordcount.WordCount
import org.apache.hadoop.io.LongWritable
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}
import no.uit.sfb.wordcount.testdata.TestData

class SparkIntegrationTest extends FunSpec with Matchers with BeforeAndAfterAll {

  var sc: SparkContext = _

  def wordCounter: WordCount = {
    val wc = new WordCount(sc)
    wc.args.inputFile = TestData.archiveWarcPath
    wc
  }

  override def beforeAll(): Unit = {
    System.clearProperty("spark.driver.port")
    System.clearProperty("spark.hostPort")

    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("test")
    sc = new SparkContext(conf)
  }

  override def afterAll(): Unit = {
    if (sc != null) {
      sc.stop()
      sc = null
      System.clearProperty("spark.driver.port")
      System.clearProperty("spark.hostPort")
    }
  }

  describe("A WARC file") {
    it("should contain the correct number of elements") {
      val warcRDD = sc.newAPIHadoopFile(TestData.archiveWarcPath, classOf[WarcFileInputFormat], classOf[LongWritable], classOf[WritableWarcRecord])
      warcRDD.count() shouldBe 36
    }
  }
  describe("WordCount") {
    it("should count the correct number of words in an RDD") {
      val wc = wordCounter
      val counts = wc.wordCount(sc.parallelize(Array("hello world", "goodbye world")))
        .collect()
        .toMap
      counts("hello") shouldBe 1
      counts("goodbye") shouldBe 1
      counts("world") shouldBe 2
    }
    it("should count the correct number of words in a WARC file") {
      val wc = wordCounter
      val counts = wc.wordCount(wc.parsedWarcContent)
        .collect()
        .toMap
      counts("free") shouldBe 7
      counts("WARC") shouldBe 4
      counts("digital") shouldBe 6
    }
  }
}
