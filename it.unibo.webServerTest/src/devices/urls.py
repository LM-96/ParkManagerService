from django.urls import path
from .views import devices_page

# Urls for the devices
urlpatterns = [
    path('', devices_page),
]