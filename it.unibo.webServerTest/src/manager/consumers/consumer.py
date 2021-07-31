from channels.generic.websocket import AsyncWebsocketConsumer
import json

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
        self.state.set(self.group_name, text_data)
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
        print(data)
        if data != None:
            if type(data) == dict:
                print(type(data))
                data = json.dumps(data)
            await self.send(data)

