import subprocess

url_fmt = "s3://aws-publicdatasets/common-crawl/crawl-data/CC-MAIN-2015-27/segments/1435376093097.69/warc/CC-MAIN-20150627033453-00%d-ip-10-179-60-89.ec2.internal.warc.gz"

for i in range(250,300):
	subprocess.call(['aws', 's3', 'cp', url_fmt % i, '.'])

