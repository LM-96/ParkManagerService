from django.urls import re_path
from .consumers.thermometer_consumer import ThermometerConsumer
from .consumers.weightsensor_consumer import WeightSensorConsumer
from .consumers.fan_consumer import FanConsumer
websocket_urlpatterns = [
    re_path(r'ws/thermometer/$', ThermometerConsumer.as_asgi()),
    re_path(r'ws/weightsensor/$', WeightSensorConsumer.as_asgi()),
    re_path(r'ws/fan/$', FanConsumer.as_asgi()),
]