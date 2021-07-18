from django.urls import path
from .views import devices_page
urlpatterns = [
    path('', devices_page),
]