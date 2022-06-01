import logging

log = logging.getLogger()
log.setLevel('DEBUG')
handler = logging.StreamHandler()
handler.setFormatter(logging.Formatter("%(asctime)s [%(levelname)s] %(name)s: %(message)s"))
log.addHandler(handler)

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')

logger = logging.getLogger(__name__)

def on_connect(client, userdata, flags, rc):
    """ The callback for when the client receives a CONNACK response from the server."""
    print('Connected with result code ' + str(rc))
    client.subscribe('topic')

cname = "Client" + str(count)
client = mqtt.Client(cname)

client.on_connect = on_connect
client.on_message = on_message
client.connect(os.getenv('MQTT_SERVER_IP'), int(os.getenv('MQTT_SERVER_PORT')), 60)
client.loop_forever()
#client.loop_start()




client.publish(topic="topic", payload=json.dumps(jsondata), qos=1, retain=False)
