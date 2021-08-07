from .consumer import ManagerConsumer
import json
# Consumer for the fan websocket
class TrolleycontrolConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'trolleycontrol_group_manager'
        
        
