import requests

class DataSender:
	def sendData(self,json):
		print(json["count"])
		requests.post('http://132.207.170.17:8088', json=json, headers = {'Content-type': 'application/json'})
		pass
