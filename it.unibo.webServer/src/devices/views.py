from django.shortcuts import render

# Renders the webpage for the devices
def devices_view(request):
    return render(request, 'devices/devices.html')