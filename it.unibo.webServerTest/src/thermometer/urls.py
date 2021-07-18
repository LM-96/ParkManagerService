from django.urls import path
from .views import th_page
urlpatterns = [
    path('', th_page),
]