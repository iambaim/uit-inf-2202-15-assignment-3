# Mandatory assignment 3 | inf-2202 | Fall 2015 

Lars Ailo Bongo (larsab@cs.uit.no), 
Inge Alexander Raknes (inge.a.raknes@uit.no). 
Ibrahim Umar (ibrahim.umar@uit.no), and 
inf-2202 fall 2015 students

Department of Computer Science,
University of Tromsø.

## Introduction

In this mandatory assignment, you will implement PageRank using Spark on Amazon Web Services (AWS), and use it to analyze data from the Common Crawl Corpus. PageRank is an algorithm used by Google to rank websites according to how many other websites link to it. Spark is becoming the de-facto standard for implementing such algorithms. AWS is a cloud platform that provides a way to execute Spark, and it has public large datasets such as the Common Crawl Corpus.

**Note!** This is the first time we use Spark and AWS in a mandatory assignment. We therefore expect issues, discovery of useful resources, and changes to this document. Please contribute by sending emails and submitting issues.

## Practicalities

This is an individual assignment, and hence you must submit an individual report. As usual, the report and code is handed in using GitHub.

## Amazon Web Services

UiT has an institution account at AWS. As a student, you will therefore receive $100 in free credit that you can use for this assignment.  To register at AWS do the following.

First, create an AWS Account:
1.	https://portal.aws.amazon.com/gp/aws/developer/registration/index.html
2.	TODO: details

Then register at AWS Educate:
3.	https://aws.amazon.com/education/awseducate/
4.	Chose: Apply for AWS Educate for Students
5.	Select that You are a student
6.	Fill in the form:
	a.	Institution name is: University of Tromsø - The Arctic University of Norway
	b.	Field of study is: Computer Science
	c.	Email: must be your uit.no address
	d.	Grade level is: Undergraduate – Intro Courses
7.	You will receive a confirmation email
8.	Your account should have $100 in credit.

Note that you have registered the account using your own private credit card. You are therefore responsible for all resource usage at AWS. If you use more than $100 worth of resources, you must pay for them.

## Spark

Spark is one of the natively supported data processing systems on Amazon Elastic MapReduce (EMR) web service. Some useful links are:
* [Spark on EMR](http://aws.amazon.com/elasticmapreduce/details/spark/)
* [EMR](http://aws.amazon.com/elasticmapreduce/)

We will describe Spark in multiple lectures in the course. Additional useful resources are:
* [Spark homepage](http://spark.apache.org/)
* [Spark paper](http://people.csail.mit.edu/matei/papers/2010/hotcloud_spark.pdf)
* [A more detailed paper](http://people.csail.mit.edu/matei/papers/2012/nsdi_spark.pdf)
* Online lectures, books, exercise, more papers, and more: http://spark.apache.org/documentation.html

You can run Spark on your own computer, uvrocks.cs.uit.no, and AWS. We recommend developing and testing code locally, and using AWS for testing your code on large datasets. Uvrocks is a small cluster where you can test Spark, and other systems in the Hadoop platform (for free).

## PageRank

PageRank is an often used algorithm for data-intensive computing, including in numerous the evaluation section in numerous academic papers. The algorithm is described in:
* [Wikipedia](https://en.wikipedia.org/wiki/PageRank)
* As a [technical report](http://ilpubs.stanford.edu:8090/422/1/1999-66.pdf)
* In a [patent](http://www.google.com/patents/US6285999)
* 
PageRank has also been used to evaluate:
* [Spark](http://people.csail.mit.edu/matei/papers/2012/nsdi_spark.pdf)
* [GraphX](https://amplab.cs.berkeley.edu/wp-content/uploads/2014/02/graphx.pdf) (Spark library)

## Common Crawl Corpus

The [common crawl corpus](http://commoncrawl.org/) provides an open repository of web crawl data. This dataset is available as an [AWS Public Data Sets](https://aws.amazon.com/datasets/common-crawl-corpus/). (For more large scale public sets check: https://aws.amazon.com/datasets/). The current archive is 149TB in size and has 1.84 billion webpages, as described in this [blog post](http://blog.commoncrawl.org/2015/10/august-2015-crawl-archive-available/).

You may not want to pay for processing the entire 149TB dataset. Instead, you need to find a reasonable sized subset of the data. In addition, you cannot download the data to your computer, but you need a test datasets to develop, test, and debug your code.

If you want to learn more about web crawlers, then a popular open source crawler is [Nutch](http://nutch.apache.org/) (from which Hadoop has its roots).

## Precode

We provide precode in this repository that demonstrate Spark and Scala on Amazon EMR, and that you can use as a starting point for your project. This includes:
* A Spark WordCount implementation (the Hello World of data-intensive programming).
* A parser library for Common Crawl Archive files.
* Scala build files.

In addition, you can use the instructions below for running WordCount on Amazon EMR.

### Build environment

1. Make sure Java 7 or 8 is installed on your system
2. Install SBT (Simple Build Tool) by following these steps: http://www.scala-sbt.org/release/tutorial/Setup.html


### Local stand-alone Spark installation for development

1. Download Spark 1.4.1 for Hadoop 2.6: https://spark.apache.org/downloads.html
2. Extract `spark-1.4.1-bin-hadoop2.6.tgz`
3. Done

### Amazon AWS cli tools

#### Get access key and secret access key
Instructions: https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-set-up.html#cli-signup

**Hint:** Don't forget to assign policy to your newly created user. Click on the "Attach Policy" button under the "Permission" section and search for the appropriate policy types. For example, in order to successfully run the provided download script below, you must assign the S3 policy (read-only or full access) to your user.

#### Install AWS cli tools
Instructions: https://docs.aws.amazon.com/cli/latest/userguide/installing.html

**tldr:** `pip install awscli`

#### Configure AWS cli tools
Instructions: https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html

**tldr:** run `aws configure`

## Download some data

A quick'n'dirty script for downloading a few GB of WARC/WET data can be found in `script/download.py` (requires *aws
cli tools* to be installed).

## Compile and test

Open the SBT console by typing `sbt` in the terminal.

To compile everything into a single JAR with all dependencies type `assembly` in the SBT console.
To run all unit and integration tests, type `test`.
To run unit tests only, type `unit:test`.

To run a command in the SBT console automatically every time a source file changes, prefix it with a `~`.
For example, the command `~unit:test` will automatically run all unit tests every time you change a file in the project.

**NOTE:** The first time you build the project SBT will download the Scala compiler and other dependencies. This takes
several minutes.

## Submit to Spark

Compile:

    sbt assembly
    
### WordCount

Submit:

    bin/spark-submit $project/target/scala-*/app.jar word-count -i /path/to/warc -o /save/output/here.txt


### PageRank

Your PageRank may be is executed in the following steps

1. Collect links from big data set

2. Run PageRank on collected links

The idea is that the input data for step 2 is significantly smaller than for step 1.

#### Step 1: Collect links

    spark-submit --driver-memory 5G --executor-memory 2G target/scala-2.10/app.jar collect-links -i 'data/warc/*.gz' -o data/links.out
    
#### Step 2: Run PageRank


    spark-submit --driver-memory 10G --executor-memory 10G target/scala-2.10/app.jar page-rank -i data/links.out -o data/page-rank.out \
        --checkpoint-dir /tmp/checkpoint \
        --edge-partitions 1000
    

--edge-partitions: The number of partitions for the web graph edges. Should be significantly larger than the number of
input partitions. If you get OutOfMemoryExceptions during runtime, try and adjust this parameter to a larger value.

--checkpoint-dir: A path to store checkpoint data. Can be a path on HDFS or S3 (according to this guide: https://www.box.com/blog/apache-spark-caching-and-checkpointing-under-hood/)


## Run on Amazon EMR

Create a public/private key-pair in the AWS EC2 console.

Start a cluster on aws:

    aws emr create-cluster --name "Spot cluster" --ami-version 3.8 --applications Name=Spark Name=Ganglia \
        --use-default-roles --ec2-attributes KeyName=INSERT-KEYNAME-HERE \
        --instance-groups InstanceGroupType=MASTER,InstanceType=c3.xlarge,InstanceCount=1,BidPrice=0.25 \
        InstanceGroupType=CORE,BidPrice=0.06,InstanceType=c3.xlarge,InstanceCount=5
        
1. Log into AWS web console for Amazon EMR to find SSH command
2. SSH to master node
3. Submit using the submit command in the previous section.
For the input parameter, specify a globbed URI of public datasets, e.g. `'s3://aws-publicdatasets/common-crawl/crawl-data/CC-MAIN-2015-27/segments/*/wet/'`
Remember the surrounding quotes around the globbed URI or else shell substitution will do funny things to it.

