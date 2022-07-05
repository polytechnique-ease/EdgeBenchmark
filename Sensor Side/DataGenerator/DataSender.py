import requests,os

class DataSender:
	def sendData(self,json):
		print(json["count"])
		requests.post('http://' + os.getenv('ClIENT_ADAPTER_IP') + ':' + os.getenv('ClIENT_ADAPTER_PORT'), json=json, headers = {'Content-type': 'application/json'})
		pass
