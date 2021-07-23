import logging
import websockets
import json
import time
from aiocoap import *
import pytest
import asyncio

class Device:

    def __init__(self, port_actor, path_actor, path_ws):
        self.host = 'localhost'
        self.port_server = '8000'
        self.port_actor = port_actor
        self.path_actor = path_actor
        self.path_ws = path_ws

        self.ws = f'ws://{self.host}:{self.port_server}/ws/{self.path_ws}/'
        self.coap = f'coap://{self.host}:{self.port_actor}/{path_actor}'

import logging
import asyncio

from aiocoap import *
import numpy as np
import socket

# @pytest.mark.asyncio
# async def test():
#     protocol = await Context.create_client_context()
#     thermometer = Device('8001', 'ctxthermometer/thermometeractor', 'thermometer')
#     values = np.random.uniform(low=-10.0, high=50.0, size=(3,))

   

#     async with websockets.connect(thermometer.ws) as websocket:

#          for v in values:

#             v = str(round(v,1))
#             print(v)

#             await websocket.send(json.dumps({'data': v}))

#             greeting = await websocket.recv()
#             print(f"{greeting}")

#             time.sleep(3)

              

#             request = Message(code=GET, uri=thermometer.coap, observe=0)

#             pr = protocol.request(request)

#             r = await pr.response

#             assert r.payload.decode("utf-8") == v



@pytest.mark.asyncio
async def test():
    protocol = await Context.create_client_context()
    fan = Device('8002', 'ctxfan/fanactor', 'fan')
    
    values = [True,False,True,False]
    
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_address = ('localhost', 8002)
    s.connect(server_address)

    for v in values:
        msg_type = 'fanon' if v else 'fanoff'
        print(msg_type)
        msg = f'msg({msg_type}, dispatch, python, fanactor, {msg_type}(ON), 1)\n'
        print(msg)
        
        byt=msg.encode()   
        s.send(byt)

        time.sleep(3)

        request = Message(code=GET, uri=fan.coap, observe=0)

        pr = protocol.request(request)

        r = await pr.response

        print(r.payload.decode("utf-8"))

        assert r.payload.decode("utf-8") == v 


# @pytest.mark.asyncio
# async def test():
#     protocol = await Context.create_client_context()
#     sonar = Device('8003', 'ctxsonar/sonaractor','sonar')
#     values = np.random.uniform(low=0, high=1000, size=(3,))

   

#     async with websockets.connect(sonar.ws) as websocket:

#          for v in values:

#             v = str(round(v))

#             print(v)

#             await websocket.send(json.dumps({'data': v}))

#             greeting = await websocket.recv()
#             print(f"{greeting}")

#             time.sleep(3)

#             request = Message(code=GET, uri=sonar.coap, observe=0)

#             pr = protocol.request(request)

#             r = await pr.response

#             print( r.payload.decode("utf-8") )

#             assert r.payload.decode("utf-8") == v

    
# @pytest.mark.asyncio
# async def test():
#     protocol = await Context.create_client_context()
#     weightsensor = Device('8004', 'ctxweightsensor/weightsensoractor','weightsensor')
#     values = np.random.uniform(low=0, high=10, size=(3,))

   

#     async with websockets.connect(weightsensor.ws) as websocket:

#          for v in values:

#             v = str(round(v))

#             print(v)

#             await websocket.send(json.dumps({'data': v}))

#             greeting = await websocket.recv()
#             print(f"{greeting}")

#             time.sleep(3)

#             request = Message(code=GET, uri=weightsensor.coap, observe=0)

#             pr = protocol.request(request)

#             r = await pr.response

#             print( r.payload.decode("utf-8") )

#             assert r.payload.decode("utf-8") == v

    