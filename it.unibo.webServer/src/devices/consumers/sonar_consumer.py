from .consumer import BasicConsumer
import json
# Consumer for the sonar websocket
class SonarConsumer(BasicConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'sonar_group'
