import logging,os
from http.server import BaseHTTPRequestHandler, HTTPServer
from BaseClientAdapter import BaseClientAdapter
from dotenv import load_dotenv
import paho.mqtt.client as mqtt
import cgi,json

load_dotenv("sensor-variables.env")

class MQTTAdapter(BaseClientAdapter):
    def _set_headers(self):
        self.send_response(HTTPStatus.OK.value)
        self.send_header('Content-type', 'application/json')
        # Allow requests from any origin, so CORS policies don't
        # prevent local development.
        self.send_header('Access-Control-Allow-Origin', '*')
        self.end_headers()

    def on_connect(client, userdata, flags, rc):
        """ The callback for when the client receives a CONNACK response from the server."""
        print('Connected with result code ' + str(rc))
        client.subscribe('topic')
    def do_POST(self):
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

        client.on_connect = self.on_connect
        client.connect(os.getenv('MQTT_SERVER_IP'), int(os.getenv('MQTT_SERVER_PORT')), 60)
        ctype, pdict = cgi.parse_header(self.headers.get('content-type'))
        
        # refuse to receive non-json content
        if ctype != 'application/json':
            self.send_response(400)
            self.end_headers()
            return
            
        # read the message and convert it into a python dictionary
        length = int(self.headers.get('content-length'))
        #msg = self.rfile.read(length).decode("utf-8")
        #message = json.loads(msg)
        #client.publish(topic="topic", payload=msg, qos=1, retain=False)
def run(server_class=HTTPServer, handler_class=MQTTAdapter, port=8089):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print('Server running at localhost:8089...')
    httpd.serve_forever()
run()