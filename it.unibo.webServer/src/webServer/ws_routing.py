from manager.consumers.antifire_consumer import AntifireConsumerManager
from manager.consumers.antifirecontrol_consumer import AntifirecontrolConsumerManager
from manager.consumers.timer_itocc_consumer import TimerITOCCConsumerManager
from django.urls import re_path
from devices.consumers.thermometer_consumer import ThermometerConsumer
from devices.consumers.weightsensor_consumer import WeightSensorConsumer
from devices.consumers.fan_consumer import FanConsumer
from devices.consumers.sonar_consumer import SonarConsumer
from manager.consumers.carparking_consumer import CarparkingConsumerManager
from manager.consumers.trolley_consumer import TrolleyConsumerManager
from manager.consumers.thermometer_consumer import ThermometerConsumerManager
from manager.consumers.timer_dtfree_consumer import TimerDTFREEConsumerManager
from manager.consumers.trolleycontrol_consumer import TrolleycontrolConsumerManager
from manager.consumers.weightsensor_consumer import WeightSensorConsumerManager
from manager.consumers.fan_consumer import FanConsumerManager
from manager.consumers.sonar_consumer import SonarConsumerManager
from manager.consumers.fancontrol_consumer import FancontrolConsumerManager


# Websockets assigned to the relative consumer
websocket_urlpatterns = [
    re_path(r'ws/thermometer/$', ThermometerConsumer.as_asgi()),
    re_path(r'ws/weightsensor/$', WeightSensorConsumer.as_asgi()),
    re_path(r'ws/fan/$', FanConsumer.as_asgi()),
    re_path(r'ws/sonar/$', SonarConsumer.as_asgi()),
    re_path(r'manager/thermometer/$', ThermometerConsumerManager.as_asgi()),
    re_path(r'manager/weightsensor/$', WeightSensorConsumerManager.as_asgi()),
    re_path(r'manager/fan/$', FanConsumerManager.as_asgi()),
    re_path(r'manager/sonar/$', SonarConsumerManager.as_asgi()),
    re_path(r'manager/carparking/$', CarparkingConsumerManager.as_asgi()),
    re_path(r'manager/timerdtfree/$', TimerDTFREEConsumerManager.as_asgi()),
    re_path(r'manager/timeritocc/$', TimerITOCCConsumerManager.as_asgi()),
    re_path(r'manager/trolley/$', TrolleyConsumerManager.as_asgi()),
    re_path(r'manager/fancontrol/$', FancontrolConsumerManager.as_asgi()),
    re_path(r'manager/trolleycontrol/$', TrolleycontrolConsumerManager.as_asgi()),
    re_path(r'manager/antifirecontrol/$', AntifirecontrolConsumerManager.as_asgi()),
    re_path(r'manager/antifire/$', AntifireConsumerManager.as_asgi()),

]


