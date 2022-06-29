from http import client
from http.server import BaseHTTPRequestHandler, HTTPServer
import json,logging,os
import cgi
from functools import partial


class YourAdapter(BaseHTTPRequestHandler):
    def __init__(self,client,b, request, client_address, server):
        self.request = request
        self.client_address = client_address
        self.server = server
        self.client = client
        self.b = b 
        print(client)

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

        print(self.client)
        print(self.b)
        return(self.client)
    def do_POST(self):
        ctype, pdict = cgi.parse_header(self.headers.get('content-type'))
        
        # refuse to receive non-json content
        if ctype != 'application/json':
            self.send_response(400)
            self.end_headers()
            return
            
        # read the message and convert it into a python dictionary
        length = int(self.headers.get('content-length'))

        message = self.rfile.read(length)

        # send the message back
        self._set_headers()
def run(server_class=HTTPServer, handler_class=YourAdapter, port=8088):
    client = "xx"
    b ="bb"
    handler = partial(YourAdapter,client,b)
    server_address = ('', port)
    httpd = server_class(server_address, handler)
    print('Server running at localhost:8088...')
    httpd.serve_forever()

run()