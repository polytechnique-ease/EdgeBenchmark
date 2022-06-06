import requests

class DataSender:

    def sendData(self,json):
    	requests.post('http://localhost:8089', json=json, headers = {'Content-type': 'application/json'}
)
    	pass
