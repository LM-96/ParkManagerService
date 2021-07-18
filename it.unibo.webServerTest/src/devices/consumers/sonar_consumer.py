from .consumer import BasicConsumer
import json

class SonarConsumer(BasicConsumer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.group_name = 'sonar_group'

   

    pass