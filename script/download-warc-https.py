import urllib

url = "https://aws-publicdatasets.s3.amazonaws.com/common-crawl/crawl-data/CC-MAIN-2015-27/segments/1435376093097.69/warc/CC-MAIN-20150627033453-00%d-ip-10-179-60-89.ec2.internal.warc.gz"


for i in range(250,251):
	temp = url % i
	print "Getting: "+ url % i
	file_name = temp.split('/')[-1]
	urllib.urlretrieve(url % i, file_name)

print "Done"
