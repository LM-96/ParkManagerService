from .consumer import ManagerConsumer
import json
# Consumer for the thermometer websocket

class TrolleyConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'trolley_group_manager'

   

