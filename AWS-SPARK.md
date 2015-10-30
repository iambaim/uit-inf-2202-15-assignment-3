# SPARK ON AWS

### !!! WARNING !!!

Before you begin using AWS, please make sure that you already getting the student credit ($100). Check your credit inside this page: https://console.aws.amazon.com/billing/home#/credits

Also, it is very important that you don't forget to **terminate** your cluster when you are done experimenting. Otherwise you are going to be billed even though the cluster is sitting idle.

#### Add user

1. Go to : https://console.aws.amazon.com/iam/home#users

2. Click “Create New Users”

3. Enter your desired user name(s)

4. After proceed, note down or download the security credentials (Access Key ID and Secret Access Key which you can use with the aws client application) and then proceed

5. Click on the newly created user -> got to “Permission” tab -> click the down arrow beside the “Inline Policies” -> click on the link after “There are no inline policies to show. To create one, …”

6. Select the “Custom Policy” and click on the “Select” button

7. Enter the policy name and  paste the text below in the “Policy Document” form
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "ABC123",
            "Effect": "Allow",
            "Action": [
                "iam:*",
                "elasticmapreduce:*",
                "s3:*"
            ],
            "Resource": [
                "*"
            ]
        }
    ]
}
```

8. Click on the “Apply Policy” button

#### Create a key-pair

1. Go to: https://console.aws.amazon.com/ec2/v2/home#KeyPairs:sort=keyName

2. Click “Create Key Pair”

3. Enter your desired key name (e.g., ACCESS) and proceed

4. A keyfile (e.g., ACCESS.pem) will be downloaded by your browser

5. You need to change the permission of the .pem : `chmod 0600 ~/ACCESS.pem`


#### Create the AWS Spark cluster

1. Create the default roles for Elastic Map Reduce (EMR)
```terminal
$ aws emr create-default-roles
```

2. Create amazon instance
```terminal
$ aws emr create-cluster --name "Spark cluster" \
  --release-label emr-4.1.0 --applications Name=Spark \
  --ec2-attributes KeyName=ACCESS --instance-type m3.xlarge \
  --instance-count 6 --use-default-roles
```
**Note:** Don't forget to check the 'KeyName' value. It should be the same key-pair name as the one you have created in the 'Create a key-pair' steps above

3. You can check your cluster status and the address for SSH connection here: https://console.aws.amazon.com/elasticmapreduce/home

4. After clicking on your cluster, click on the "SSH" link to get information on how to connect to your shiny new cluster using SSH

#### Run your spark program

1. Setup the aws cli: `aws configure`. Use the security credentials from the "Add User"
steps above.

1. Copy your assignment-3 files (including the compiled jar files) to the cluster (use the full command provided in your cluster SSH information page, below are just an example)
```terminal
$ scp -r -i ~/Downloads/ACCESS.pem assignment-3/target/scala-* hadoop@ec2-54-111-11-111.compute-1.amazonaws.com:~/
```
**Note:** If the compiled jar files are included then you don't need to compile the application again inside the cluster

2. Login into your cluster
```terminal
$ ssh -i ~/Downloads/ACCESS.pem hadoop@ec2-54-111-11-111.compute-1.amazonaws.com
```

3. Prepare your data, you use the scripts provided in the `script/` directory. To take advantage of the multiple nodes, you can enlarge the range inside the script so that the script downloads more than 1 file

4. Upload your data to HDFS `hdfs dfs -copyFromLocal *.gz`

5. Run spark
```terminal
$ spark-submit --num-executors 5 assignment-3/target/scala-2.10/app.jar collect-links -i *gz -o output/cl1
```

#### Further information

AWS EMR Spark: http://docs.aws.amazon.com/ElasticMapReduce/latest/ReleaseGuide/emr-spark.html


#### Done!
