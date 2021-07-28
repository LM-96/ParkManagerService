from django.urls import path
from .views import carenter, client_view, notify_interest, pickup

# Urls for the devices
urlpatterns = [
    path('', client_view),
    path('notifyinterest', notify_interest),
    path('carenter', carenter),
    path('pickup', pickup),
]