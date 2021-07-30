from .consumer import ManagerConsumer
import json
# Consumer for the weight sensor websocket

class WeightSensorConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'weightsensor_group'

