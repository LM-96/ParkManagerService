from channels.generic.websocket import AsyncWebsocketConsumer
import json

class ThermometerConsumer(AsyncWebsocketConsumer):

    async def connect(self):
        self.group_name = 'thermometer_group'

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
        temperature = msg_json['temperature']
        await self.channel_layer.group_send(
            self.group_name,
            {
                'type': 'temp_message',
                'temperature': temperature,
            }
        )

    async def temp_message(self, event):
        temperature = event['temperature']

        await self.send(text_data=json.dumps({
            'temperature': temperature
        }))


    pass