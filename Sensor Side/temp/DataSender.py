import requests

class DataSender:

    def sendData(self,json):
    	requests.post('http://localhost:8088', json)
    	pass
