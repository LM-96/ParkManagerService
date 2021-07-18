from .consumer import BasicConsumer
import json

class FanConsumer(BasicConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'fan_group'

   
