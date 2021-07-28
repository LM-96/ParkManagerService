from django.urls import path
from .views import devices_view

# Urls for the devices
urlpatterns = [
    path('', devices_view),
]