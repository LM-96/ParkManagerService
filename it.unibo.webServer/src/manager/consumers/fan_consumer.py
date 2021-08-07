from .consumer import ManagerConsumer
import json
# Consumer for the fan websocket
class FanConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'fan_group_manager'
        
        
