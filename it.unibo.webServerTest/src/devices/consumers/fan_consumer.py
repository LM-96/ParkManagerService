from .consumer import BasicConsumer
import json

# Consumer for the fan websocket

class FanConsumer(BasicConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'fan_group'
        self.state = None
