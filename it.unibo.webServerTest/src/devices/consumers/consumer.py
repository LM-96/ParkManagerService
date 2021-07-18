from channels.generic.websocket import AsyncWebsocketConsumer
import json

class BasicConsumer(AsyncWebsocketConsumer):

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
        msg_json = json.loads(text_data)
        if 'data' in msg_json:
            data = msg_json['data']
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

    pass