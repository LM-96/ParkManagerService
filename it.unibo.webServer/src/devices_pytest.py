import websockets
import json
import time
from aiocoap import *
import pytest
import yaml
import munch
import numpy as np
import socket

class Device:

    def __init__(self, server, server_port, port_actor, context, actor, name):
        self.server = server
        self.server_port = server_port
        self.port_actor = port_actor
        self.actor = actor
        self.context = context
        self.name = name

        self.ws = f'ws://{self.server}:{self.server_port}/ws/{self.name}/'
        self.coap = f'coap://{self.server}:{self.port_actor}/{self.context}/{self.actor}'

with open("config/config.yaml", 'r') as yaml_file:
    yaml_dict = yaml.load(yaml_file, yaml.FullLoader)
    config = munch.munchify(yaml_dict)


socket_fan = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket_fan.connect( (config.system.host, config.fan.port))
socket_sonar = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket_sonar.connect( (config.system.host, config.sonar.port))
socket_sensor = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket_sensor.connect( (config.system.host, config.weightsensor.port))


@pytest.mark.asyncio
async def test_thermometer():
    protocol = await Context.create_client_context()
    thermometer = Device(config.system.host, config.system.port, config.thermometer.port, 
                         config.thermometer.ctx, config.thermometer.actor, config.thermometer.name)
    values = np.random.uniform(low=-10.0, high=50.0, size=(3,))

   

    async with websockets.connect(thermometer.ws) as websocket:

         for v in values:

            v = str(round(v,1))
            print(v)

            await websocket.send(json.dumps({'data': v}))

            time.sleep(2.5)
              

            request = Message(code=GET, uri=thermometer.coap, observe=0)

            pr = protocol.request(request)

            r = await pr.response

            response = r.payload.decode("utf-8") 

            jr = json.loads(response)    

            assert jr["data"] == v



@pytest.mark.asyncio
async def test_fan():
    global socket_fan
    protocol = await Context.create_client_context()
    fan = Device(config.system.host, config.system.port, config.fan.port, 
                         config.fan.ctx, config.fan.actor, config.fan.name)
    
    values = [True,False,True,False]


    for v in values:
        msg_type = 'fanon' if v else 'fanoff'
        msg = f'msg({msg_type}, dispatch, python, fanactor, {msg_type}(ON), 1)\n'
        
        byt=msg.encode()   
        socket_fan.send(byt)

        time.sleep(1)

        request = Message(code=GET, uri=fan.coap, observe=0)

        pr = protocol.request(request)

        r = await pr.response

        print(r.payload.decode("utf-8"))

        assert r.payload.decode("utf-8") == ("ON" if v else "OFF") 


@pytest.mark.asyncio
async def test_sonar():
    global socket_sonar
    protocol = await Context.create_client_context()
    sonar = Device(config.system.host, config.system.port, config.sonar.port, 
                         config.sonar.ctx, config.sonar.actor, config.sonar.name)
    values = np.random.uniform(low=0, high=1000, size=(3,))

    msg = f'msg(dopolling, dispatch, python, {config.sonar.actor}, dopolling(1000), 1)\n'
    byt=msg.encode()   
    socket_sonar.send(byt)
        

    async with websockets.connect(sonar.ws) as websocket:
        time.sleep(3)
        for v in values:

            v = str(round(v))

            print(v)

            await websocket.send(json.dumps({'data': v}))

            time.sleep(1)

            request = Message(code=GET, uri=sonar.coap, observe=0)

            pr = protocol.request(request)

            r = await pr.response

            response = r.payload.decode("utf-8") 

            jr = json.loads(response)     

            print(jr)       

            assert jr['data'] == v

    msg = f'msg(stoppolling, dispatch, python, {config.sonar.actor}, stoppolling(STOP), 1)\n'
    byt=msg.encode()   
    socket_sonar.send(byt) 


@pytest.mark.asyncio
async def test_weightsensor():
    global socket_sensor

    protocol = await Context.create_client_context()
    weightsensor = Device(config.system.host, config.system.port, config.weightsensor.port, 
                         config.weightsensor.ctx, config.weightsensor.actor, config.weightsensor.name)
    values = np.random.uniform(low=0, high=10, size=(3,))

    msg = f'msg(dopolling, dispatch, python, {config.weightsensor.actor}, dopolling(1000), 1)\n'
    byt=msg.encode()   
    socket_sensor.send(byt)
        

    async with websockets.connect(weightsensor.ws) as websocket:

        for v in values:

            v = str(round(v,1))

            print(v)

            await websocket.send(json.dumps({'data': v}))

            time.sleep(1)

            request = Message(code=GET, uri=weightsensor.coap, observe=0)

            pr = protocol.request(request)

            r = await pr.response

            response = r.payload.decode("utf-8") 

            jr = json.loads(response)   

            assert jr['data'] == v

    msg = f'msg(stoppolling, dispatch, python,  {config.weightsensor.actor}, stoppolling(STOP), 1)\n'
    byt=msg.encode()   
    socket_sensor.send(byt)

    
@pytest.mark.asyncio
async def test_antifire():
    protocol = await Context.create_client_context()
    thermometer = Device(config.system.host, config.system.port, config.thermometer.port, 
                         config.thermometer.ctx, config.thermometer.actor, config.thermometer.name)
    fan = Device(config.system.host, config.system.port, config.fan.port, 
                         config.fan.ctx, config.fan.actor, config.fan.name)

    values = [0,50,0,50]

   

    async with websockets.connect(thermometer.ws) as websocket:

         for v in values:

            v = str(v)

            await websocket.send(json.dumps({'data': v}))

            time.sleep(2.5)
              

            request = Message(code=GET, uri=fan.coap, observe=0)

            pr = protocol.request(request)

            r = await pr.response

            assert r.payload.decode("utf-8") == ('ON' if v == 0 else "OFF")