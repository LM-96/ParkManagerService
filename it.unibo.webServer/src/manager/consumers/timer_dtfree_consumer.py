from .consumer import ManagerConsumer
import json
# Consumer for the thermometer websocket

class TimerDTFREEConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'timer_dtfree_group_manager'

   

