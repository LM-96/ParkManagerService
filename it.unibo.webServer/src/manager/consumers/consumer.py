import socket
import json
from manage import config
from channels.generic.websocket import AsyncWebsocketConsumer

from manager.state import State

# Base consumer class. It handles messages from/to the socket 

class ManagerConsumer(AsyncWebsocketConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.state = State()

    async def connect(self):

        await self.channel_layer.group_add(
            self.group_name,
            self.channel_name 
        )
        
        await self.accept()
        

        await self.channel_layer.group_send(
            self.group_name,
            {
                'type': 'data_message',
                'data': self.state.get(self.group_name), #### Inserisci aggiornamento stato dal singleton
            }
        ) 


    async def disconnect(self, code):
        await self.channel_layer.group_discard(
            self.group_name,
            self.channel_name
        )


    async def receive(self, text_data):
        print(text_data)
        
        if self.group_name == "fancontrol_group_manager":
            socket_fan = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            socket_fan.connect( (config.system.host, config.fan.port))
            text_json = json.loads(text_data)
            msg_type = 'fanon' if text_json['data'] == "ON" else 'fanoff'
            msg = f'msg({msg_type}, dispatch, python, {config.fan.actor}, {msg_type}(ON), 1)\n'
            
            byt=msg.encode()   
            socket_fan.send(byt)
            print("MESSAGE SENT FROM WS")
            socket_fan.close()
        elif self.group_name == "trolleycontrol_group_manager":
            socket_trolley = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            socket_trolley.connect((config.system.host, config.trolley.port))
            text_json = json.loads(text_data)
            msg_type = 'starttrolley' if text_json['data'] == "ON" else 'stoptrolley'
            msg = f'msg({msg_type}, dispatch, python, {config.trolley.actor}, {msg_type}(ON), 1)\n'
            
            byt=msg.encode()   
            socket_trolley.send(byt)
            print("MESSAGE SENT FROM WS")
            socket_trolley.close()
        else:
            self.state.set(self.group_name, text_data)

        await self.channel_layer.group_send(
            self.group_name,
            {
                'type': 'data_message',
                'data': text_data,
            }
        ) 

    async def data_message(self, event):
        data = event['data']
        if data != None:
            if type(data) == dict:
                print(type(data))
                data = json.dumps(data)
            await self.send(data)

