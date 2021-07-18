from django.shortcuts import render

# Create your views here.
def th_page(request):
    return render(request, 'thermometer/th.html', context={'text': 'Hello'})