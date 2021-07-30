from channels.generic.websocket import AsyncWebsocketConsumer
import json

from devices.state import State

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


    async def disconnect(self, code):
        await self.channel_layer.group_discard(
            self.group_name,
            self.channel_name
        )


    async def receive(self, text_data):
        print(text_data)
        #msg_json = json.loads(text_data)
        #print(msg_json)
        await self.channel_layer.group_send(
            self.group_name,
            {
                'type': 'data_message',
                'data': text_data,
            }
        ) 

    async def data_message(self, event):
        print(event)
        data = event['data']
        await self.send(data)

