from .consumer import ManagerConsumer
import json
# Consumer for the fan websocket
class FancontrolConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'fancontrol_group_manager'
        
        
