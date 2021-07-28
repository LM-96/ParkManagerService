from django.urls import path
from .views import login_view, status_view, registration_view, logout_view

# Urls for the devices
urlpatterns = [
    path('', status_view, name="status"),
    path('login', login_view, name="login"),
    # path('register', registration_view, name="register"),
    path('logout', logout_view, name="logout"),
]