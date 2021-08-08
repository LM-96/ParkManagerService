from .consumer import ManagerConsumer
import json
# Consumer for the fan websocket
class AntifirecontrolConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'antifirecontrol_group_manager'
        
        
