from .consumer import ManagerConsumer
import json
# Consumer for the thermometer websocket

class RobotConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'robot_group_manager'

   

