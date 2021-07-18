from django.urls import re_path
from .consumers import ThermometerConsumer
websocket_urlpatterns = [
    re_path(r'ws/thermometer/$', ThermometerConsumer.as_asgi())
]