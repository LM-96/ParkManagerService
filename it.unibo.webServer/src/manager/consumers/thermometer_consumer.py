from .consumer import ManagerConsumer
import json
# Consumer for the thermometer websocket

class ThermometerConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'thermometer_group_manager'

   

