import json
from pyspark import SparkContext
from pyspark.streaming import StreamingContext
from mqtt import MQTTUtils

sc = SparkContext(appName="sensors")
sc.setLogLevel("ERROR")
ssc = StreamingContext(sc, 1)
ssc.checkpoint("checkpoint")

# broker URI
brokerUrl = "tcp://localhost:1883" # "tcp://iot.eclipse.org:1883"
# topic or topic pattern where temperature data is being sent
topic = "topic"

mqttStream = MQTTUtils.createStream(ssc, brokerUrl, topic, username=None, password=None)
mqttStream = mqttStream.map(lambda js: json.loads(js))
# # convert from json into a Python dict

mqttStream = mqttStream \
   .filter(lambda message: ((message['size'] < 148000) and (message['size'] > 141000)))

def printSomething(time, rdd):
    c = rdd.collect()
    print("-------------------------------------------")
    print("Time: %s" % time)
    print("-------------------------------------------")
    
    for record in c:
        # "draw" our lil' ASCII-based histogram
        print(record['count'])
    print("")
    
mqttStream.foreachRDD(printSomething)


ssc.start()
ssc.awaitTermination()