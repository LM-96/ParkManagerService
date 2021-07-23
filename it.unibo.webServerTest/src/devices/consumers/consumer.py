from channels.generic.websocket import AsyncWebsocketConsumer
import json

from devices.state import State

# Base consumer class. It handles messages from/to the socket 

class BasicConsumer(AsyncWebsocketConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.state = State()

    async def connect(self):

        await self.channel_layer.group_add(
            self.group_name,
            self.channel_name 
        )
        
        await self.accept()

        print(self.group_name + " " + self.state.get(self.group_name))

        await self.channel_layer.group_send(
                self.group_name,
                {
                    'type': 'data_message',
                    'data': self.state.get(self.group_name),
                }
            ) 


    async def disconnect(self, code):
        await self.channel_layer.group_discard(
            self.group_name,
            self.channel_name
        )


    async def receive(self, text_data):
        msg_json = json.loads(text_data)
        if 'data' in msg_json:
            data = msg_json['data']
            self.state.set(self.group_name, data)
            print(self.state.get(self.group_name))
            await self.channel_layer.group_send(
                self.group_name,
                {
                    'type': 'data_message',
                    'data': data,
                }
            ) 

    async def data_message(self, event):
        data = event['data']
        await self.send(text_data=json.dumps({
            'data': data
        }))

