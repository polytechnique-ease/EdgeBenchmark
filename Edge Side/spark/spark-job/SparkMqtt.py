#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""
 This example will consume temperature data (or any other numerical values, really)
 from an MQTT broker, and consolidate/graph this data on a 15-second sliding window.
 This work is based on the original mqtt_wordcount.py sample from the Apache Spark codebase
 Running the example:
    `$ bin/spark-submit --jars \
      external/mqtt-assembly/target/spark-streaming-mqtt-assembly_*.jar \
      mqtt_spark_streaming.py`
"""


def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False

import sys
import operator

from pyspark import SparkContext
from pyspark.streaming import StreamingContext
from mqtt import MQTTUtils

sc = SparkContext(appName="sensorsApp")
ssc = StreamingContext(sc, 1)
ssc.checkpoint("checkpoint")

# broker URI
brokerUrl = "tcp://132.207.170.59:1883" 
# topic or topic pattern where temperature data is being sent
topic = "topic"

mqttStream = MQTTUtils.createStream(ssc, brokerUrl, topic, username=None, password=None)

def printSomething(time, rdd):
    print()

mqttStream.foreachRDD(printSomething)


ssc.start()
ssc.awaitTermination()
