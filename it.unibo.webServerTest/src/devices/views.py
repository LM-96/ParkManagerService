from django.shortcuts import render

# Renders the webpage for the devices
def devices_page(request):
    return render(request, 'devices/devices.html')