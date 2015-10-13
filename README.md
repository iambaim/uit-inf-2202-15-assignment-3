# WordCount 
##### (by: Inge Alexander Raknes)

This is an example project to demonstrate Spark and Scala on Amazon EMR.

## Setup

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

PageRank is executed in the following steps

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

