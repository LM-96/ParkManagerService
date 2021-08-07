from .consumer import ManagerConsumer
import json
# Consumer for the thermometer websocket

class TimerITOCCConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'timer_itocc_group_manager'

   

