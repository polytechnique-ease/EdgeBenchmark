from http.server import BaseHTTPRequestHandler, HTTPServer
import json,logging,os,sys
import cgi
import paho.mqtt.client as mqtt
from dotenv import load_dotenv
from functools import partial

load_dotenv("sensor-variables.env")



class Server(BaseHTTPRequestHandler):
    def __init__(self,client, request, client_address, server):
        self.request = request
        self.client_address = client_address
        self.server = server
        self.client = client

        super().__init__(request,client_address,server)
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()      
    def do_HEAD(self):
        self._set_headers()
    def do_GET(self):
        self._set_headers()
        print(self.path)
        print(parse_qs(self.path[2:]))
    def do_POST(self):
        ctype, pdict = cgi.parse_header(self.headers.get('content-type'))
        
        # refuse to receive non-json content
        if ctype != 'application/json':
            self.send_response(400)
            self.end_headers()
            return
            
        # read the message and convert it into a python dictionary
        length = int(self.headers.get('content-length'))

        message = json.loads(self.rfile.read(length))

        self.client.publish(topic="topic", payload=json.dumps(message), qos=1, retain=False)
        # send the message back
        self._set_headers()
def on_connect(client, userdata, flags, rc):
    """ The callback for when the client receives a CONNACK response from the server."""
    print('Connected with result code ' + str(rc))  
def run(server_class=HTTPServer, handler_class=Server, port=8088):
    log = logging.getLogger()
    log.setLevel('DEBUG')
    handler = logging.StreamHandler()
    handler.setFormatter(logging.Formatter("%(asctime)s [%(levelname)s] %(name)s: %(message)s"))
    log.addHandler(handler)

    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')

    logger = logging.getLogger(__name__)
    #cname = "Client" + str(count)
    cname = "Clientaa" 

    client = mqtt.Client(cname)

    client.on_connect = on_connect
    client.connect(os.getenv('MQTT_SERVER_IP'), int(os.getenv('MQTT_SERVER_PORT')), 60)
    client.loop_start()
    handler = partial(Server,client)
    server_address = ('', port)
    httpd = server_class(server_address, handler)
    print('Server running at localhost:8088...')
    httpd.serve_forever()

run()