
import cv2
from datetime import *
import time
import logging
import base64
import sys
import os
import shutil
import ast

import paho.mqtt.client as mqtt
import datetime
import sys
import re
from typing import NamedTuple

import json

from dotenv import load_dotenv

load_dotenv("sensor-variables.env")

camera_id = os.getenv('CAMERA_ID') # sys.argv[1]  # 123
#JPGQuality = os.getenv('JPGQUALITY')#int(sys.argv[3] ) # 20
JPGQuality = int(os.getenv('JPGQUALITY'))
transmitdelay = os.getenv('TRANSMITDELAY') # int(sys.argv[4])  # 10

log = logging.getLogger()
log.setLevel('DEBUG')
handler = logging.StreamHandler()
handler.setFormatter(logging.Formatter("%(asctime)s [%(levelname)s] %(name)s: %(message)s"))
log.addHandler(handler)

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')

logger = logging.getLogger(__name__)
print('Hello 1')


def on_connect(client, userdata, flags, rc):
    """ The callback for when the client receives a CONNACK response from the server."""
    print('Connected with result code ' + str(rc))
# The callback for when a PUBLISH message is received from the server.

def myconverter(o):
    if isinstance(o, datetime.datetime):
        return o.__str__()
class Camera():
    def __init__(self,camera_id,JPGQuality,transmitdelay, folder):
        self.camera_id = camera_id
        self.JPGQuality = JPGQuality
        self.transmitdelay = transmitdelay
        start = time.time()
        self.folder = folder



    def cleanup(self):


        folder = './imagesout'
        for the_file in os.listdir ('./imagesout'):
            file_path = os.path.join ('./imagesout', the_file)
            try:
                if os.path.isfile (file_path):
                    os.unlink (file_path)
            # elif os.path.isdir(file_path): shutil.rmtree(file_path)
            except Exception as e:
                print (e)



    def processVideoStream(self, thread=0):



            vidcap = cv2.VideoCapture('black.mp4')
            success, image = vidcap.read ()
            count = 0
            success = True

            day_date= date.today()

            start = time.time ()
            #i = self.JPGQuality
            print('JPGQuality:', self.JPGQuality)


            list_image_base64 = []


            list_image_base64_str = ''
            image_base64_last = ''
            cname = "Client" + self.camera_id
            client = mqtt.Client(cname)

            client.on_connect = on_connect
            client.connect(os.getenv('MQTT_SERVER_IP'), int(os.getenv('MQTT_SERVER_PORT')), 60)

            while success:
                #for i in range(9):
                #self.JPGQuality = i + 1
                cv2.imwrite("./imagesout/frame%d.jpg" % count, image, [int(cv2.IMWRITE_JPEG_QUALITY), self.JPGQuality])  # save frame as JPEG file
                imageFileNameandPath =  ("./imagesout/frame%d.jpg" % count)
                image_base64 = self.convertToBase64(imageFileNameandPath)
                success, image = vidcap.read ()
                print ('Read a new frame: ', success,  ' thread number:', thread)

                timestamp = str(time.time())
                frame_id = timestamp+str(count)
                end = time.time()
                runtime_seconds = end - start
                data = {'camera_id':str(self.camera_id), 'frame_id':str(frame_id), 'timestamp':timestamp, 'duration':str(int(runtime_seconds)) }
                #self.cassandraclient.saveToCassandra(self.camera_id, frame_id, timestamp,day_date ,image_base64)
                #self.kafkaclient.saveToKafka(self.camera_id, frame_id, timestamp, day_date, image_base64)

                #list_image_base64.append(str(image_base64))
                list_image_base64_str += str(image_base64)+'XXX'
                image_base64_last = str(image_base64)

                jsondata = {}
                jsondata['size'] =  os.stat(imageFileNameandPath).st_size
                jsondata['camera_id'] =  camera_id
                jsondata['transmitdelay'] =  transmitdelay
                jsondata['JPGQuality'] =  JPGQuality
                jsondata['count'] =  count
                jsondata['frame_id'] = str(frame_id)
                jsondata['sent_time'] = timestamp
                jsondata['value'] = str(image_base64)
                client.publish(topic="topic", payload=json.dumps(jsondata), qos=1, retain=False)

                time.sleep(1)

                count += 1

                print('Experiment Runtime (seconds): ' + str(int(runtime_seconds)))
                print('Images written per (second): ' + str(count/runtime_seconds))

            client.disconnect()
            self.cleanup()


    def convertToBase64(self,fileNameandPath):

        with open(fileNameandPath, "rb") as imageFile:
            str = base64.b64encode(imageFile.read())
        return str







camera = Camera(camera_id, JPGQuality, transmitdelay, './imagesout')
camera.processVideoStream()
