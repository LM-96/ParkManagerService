from channels.generic.websocket import AsyncWebsocketConsumer
import json

# Base consumer class. It handles messages from/to the socket 

class BasicConsumer(AsyncWebsocketConsumer):

    
    async def update_on_connect(self):
        if self.state != None:
            await self.channel_layer.group_send(
                    self.group_name,
                    {
                        'type': 'data_message',
                        'data': self.state,
                    }
                ) 

    async def connect(self):

        await self.channel_layer.group_add(
            self.group_name,
            self.channel_name 
        )
        
        await self.accept()

        await self.update_on_connect()


    async def disconnect(self, code):
        await self.channel_layer.group_discard(
            self.group_name,
            self.channel_name
        )

    async def receive(self, text_data):
        msg_json = json.loads(text_data)
        if 'data' in msg_json:
            data = msg_json['data']
            self.state = data if self.state != None else None
            print("LOLOLOLOLOL " + self.state)
            await self.update_on_connect()

    async def data_message(self, event):
        data = event['data']
        
        await self.send(text_data=json.dumps({
            'data': data
        }))

