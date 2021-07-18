from .consumer import BasicConsumer
import json

class ThermometerConsumer(BasicConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'thermometer_group'

   

    pass