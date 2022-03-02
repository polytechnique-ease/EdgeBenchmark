import json
import time
from pyspark import SparkContext
from pyspark.streaming import StreamingContext
from mqtt import MQTTUtils
import ast , os 
from influxdb import InfluxDBClient
from dotenv import load_dotenv
import sys

load_dotenv("spark-variables.env")

def _init_influxdb_database():
    databases = influx_client.get_list_database()
    if len(list(filter(lambda x: x['name'] == INFLUXDB_DATABASE, databases))) == 0:
        influx_client.create_database(INFLUXDB_DATABASE)
    influx_client.switch_database(INFLUXDB_DATABASE)

# The callback for when a PUBLISH message is received from the server.
def save_influx(jsondata_body, body):
    print(" Saving data of : ", sys.getsizeof(str(body)), ' bytes')
    jsondata_body["fields"]["beforeInfluxDB"] = str(time.time())
    influx_client.write_points(jsondata_body)
INFLUXDB_DATABASE = os.getenv('INFLUXDB_DATABASE_NAME')

influx_client = InfluxDBClient(os.getenv('INFLUXDB_DATABASE_IP'), os.getenv('INFLUXDB_DATABASE_PORT'), database=INFLUXDB_DATABASE)


_init_influxdb_database()

def on_RDD(data,recieved_time):

    jsondata_body = {
        "measurement": "t_spark_test1",
        "tags": {
            "camera_id": data['camera_id'],
        },
        "transmitdelay":data['transmitdelay'],
       "JPGQuality":data['JPGQuality'],
        "fields": {
            "beforeSpark_time": recieved_time,
            "frame_id": data['frame_id'],
            "FromSensor_time": data['sent_time'],
            "value": data['value']
        }
    }
    save_influx(jsondata_body, str(data))



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

      
def printSomething(beforesparktime, rdd):
    c = rdd.collect()
    print("-------------------------------------------")
    print("Time: %s" % time)
    print("-------------------------------------------")
    
    for record in c:
        # "draw" our lil' ASCII-based histogram
        on_RDD(record,str(beforesparktime.timestamp()))
    print("")
    
mqttStream.foreachRDD(printSomething)


ssc.start()
ssc.awaitTermination()