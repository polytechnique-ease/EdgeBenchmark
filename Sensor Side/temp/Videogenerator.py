import Idatagenerator
import os, cv2,json
from datetime import *
import paho.mqtt.client as mqtt

class VideoGenerator(Idatagenerator):


    def __init__(self, datasender,camera_id,JPGQuality,transmitdelay, folder):
        self.datasender = datasender
        self.camera_id = camera_id
        self.JPGQuality = JPGQuality
        self.transmitdelay = transmitdelay
        self.folder = folder   

    def cleanup(self):
        folder = './imagesout'
        for the_file in os.listdir ('./imagesout'):
            file_path = os.path.join ('./imagesout', the_file)
            try:
                if os.path.isfile (file_path):
                    os.unlink (file_path)
            except Exception as e:
                print (e)
    def generateData(self):

            vidcap = cv2.VideoCapture('black.mp4')
            success, image = vidcap.read ()
            count = 0
            success = True

            start = time.time ()
            print('JPGQuality:', self.JPGQuality)
            list_image_base64_str = ''
            while success:
                cv2.imwrite("./imagesout/frame%d.jpg" % count, image, [int(cv2.IMWRITE_JPEG_QUALITY), self.JPGQuality])  # save frame as JPEG file
                imageFileNameandPath =  ("./imagesout/frame%d.jpg" % count)
                image_base64 = self.convertToBase64(imageFileNameandPath)
                success, image = vidcap.read ()
                print ('Read a new frame: ', success)
                
                timestamp = str(time.time())
                frame_id = timestamp+str(count)
                end = time.time()
                runtime_seconds = end - start
                list_image_base64_str += str(image_base64)+'XXX'

                jsondata = {}
                jsondata['size'] =  os.stat(imageFileNameandPath).st_size
                jsondata['camera_id'] =  self.camera_id
                jsondata['transmitdelay'] =  self.transmitdelay
                jsondata['JPGQuality'] =  self.JPGQuality
                jsondata['count'] =  count
                jsondata['frame_id'] = str(frame_id)
                jsondata['FromSensor_time'] = timestamp
                jsondata['value'] = str(image_base64)
                jsondata['measurement_name'] = "cmode100_53"
                self.datasender.sendData(jsondata)

                print('Experiment Runtime (seconds): ' + str(int(runtime_seconds)))
                print('Images written per (second): ' + str(count/runtime_seconds))


            self.cleanup()