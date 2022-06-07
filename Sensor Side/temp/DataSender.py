import requests

class DataSender:
	def sendData(self,json):
		print(json["count"])
		requests.post('http://localhost:8088', json=json, headers = {'Content-type': 'application/json'})
		pass
