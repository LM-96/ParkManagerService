from .consumer import ManagerConsumer
import json
# Consumer for the sonar websocket
class SonarConsumerManager(ManagerConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'sonar_group'
