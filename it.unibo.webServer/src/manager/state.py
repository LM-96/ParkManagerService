import json


class Singleton(type):
    _instances = {}
    def __call__(cls, *args, **kwargs):
        if cls not in cls._instances:
            cls._instances[cls] = super(Singleton, cls).__call__(*args, **kwargs)
        return cls._instances[cls]


class State(metaclass=Singleton):
    def __init__(self):
        self.thermometer_state = {"data": "18"}
        self.robot_coords = {
            "position": {
                "x": "0",
                "y": "0"
            },
            "state": "OCCUPIED"
        }
        self.sonar_state = {"data": "1000"}
        self.weightsensor_state = {"data": "0"}
        self.fan_state = 'OFF'
        self.carparking = {
            "indoor": {
                "state": "FREE",
                "user": ""
            },
            "outdoor": {
                "state": "FREE",
                "user": ""
            },
            "1": {
                "state": "FREE",
                "user": ""
            },
            "2": {
                "state": "FREE",
                "user": ""
            },
            "3": {
                "state": "FREE",
                "user": ""
            },
            "4": {
                "state": "FREE",
                "user": ""
            },
            "5": {
                "state": "FREE",
                "user": ""
            },
            "6": {
                "state": "FREE",
                "user": ""
            },
        }

    def set(self, name, data):
        if name == "fan_group_manager":
            self.fan_state = data
        elif name == "weightsensor_group_manager":
            self.weightsensor_state = data
        elif name == "sonar_group_manager":
            self.sonar_state = data
        elif name == "thermometer_group_manager":
            self.thermometer_state = data
        elif name == "carparking_group_manager":
            json_data = json.loads(data)
            for key, value in json_data.items():
                self.carparking[key] = value
        elif name == "robot_group_manager":
            self.robot_coords = data

    def get(self, name):
        if name == "fan_group_manager":
            return self.fan_state
        elif name == "weightsensor_group_manager":
            return self.weightsensor_state
        elif name == "sonar_group_manager":
            return self.sonar_state
        elif name == "thermometer_group_manager":
            return self.thermometer_state
        elif name == "carparking_group_manager":
            return self.carparking
        elif name == "robot_group_manager":
            return self.robot_coords

        return None
    
