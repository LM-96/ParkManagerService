from django.shortcuts import render

# Renders the webpage for the devices
def home_view(request):
    return render(request, 'home.html')