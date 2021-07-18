from django.shortcuts import render

# Create your views here.
def devices_page(request):
    return render(request, 'devices/devices.html')