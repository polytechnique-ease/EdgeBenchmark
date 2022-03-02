from datetime import *
import time

import paho.mqtt.client as mqtt




def on_connect(client, userdata, flags, rc):
    """ The callback for when the client receives a CONNACK response from the server."""
    print('Connected with result code ' + str(rc))
    client.subscribe('topic')



cname = "Mahsa 1 "
client = mqtt.Client(cname)

client.on_connect = on_connect
client.connect('localhost',1883 ,60)

for i in range(10):
    client.publish(topic="topic", payload=str(i), qos=1, retain=False)