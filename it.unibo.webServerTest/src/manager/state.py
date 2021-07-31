class Singleton(type):
    _instances = {}
    def __call__(cls, *args, **kwargs):
        if cls not in cls._instances:
            cls._instances[cls] = super(Singleton, cls).__call__(*args, **kwargs)
        return cls._instances[cls]


class State(metaclass=Singleton):
    def __init__(self):
        self.thermometer_state = '18'
        self.robot_coords = {
            "position": "0,0",
            "state": "OCCUPIED"
        }
        self.sonar_state = '1000'
        self.weightsensor_state = '0'
        self.fan_state = 'OFF'
        self.carparking = {
            "indoor": "FREE",
            "outdoor": "FREE",
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

    def set(self, name, value):
        if name == "fan_group":
            self.fan_state = value
        elif name == "weightsensor_group":
            self.weightsensor_state = value
        elif name == "sonar_group":
            self.sonar_state = value
        elif name == "thermometer_group":
            self.thermometer_state = value
        elif name == "carparking_group":
            pass
        elif name == "robot_group":
            self.robot_coords = value

    def get(self, name):
        if name == "fan_group":
            return self.fan_state
        elif name == "weightsensor_group":
            return self.weightsensor_state
        elif name == "sonar_group":
            return self.sonar_state
        elif name == "thermometer_group":
            return self.thermometer_state
        elif name == "carparking_group":
            return self.carparking
        elif name == "robot_group":
            return self.robot_coords

        return None
    

