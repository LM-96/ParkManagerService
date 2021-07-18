
from .consumer import BasicConsumer
import json

class WeightSensorConsumer(BasicConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'weightsensor_group'


    pass