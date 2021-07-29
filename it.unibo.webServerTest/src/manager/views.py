import asyncio
from collections import namedtuple
import threading
from aiocoap import *
from aiocoap.message import Message
from aiocoap.protocol import Context
from django import forms
from django.shortcuts import render, redirect
#from coapthon.client.helperclient import HelperClient
from six import print_

from manager.thread_coap import ThreadCoap, ThreadFarm
from .forms import ManagerRegistrationForm
from django.contrib import messages
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from manage import config
from asgiref.sync import async_to_sync
# Create your views here.


login_required(login_url='login')
def status_view(request):
    
    rows = ""
    for i in range(0,int(config.map.rows)):
        rows += str(i)

    cols = ""
    for i in range(0,int(config.map.cols)):
        cols += str(i)
    constext = {
        'rows': rows,
        'cols': cols,
        'parking_slots': config.map.parking_slots
    }

    return render(request, 'manager/status.html', constext)

def login_view(request):
    if request.user.is_authenticated:
        return redirect('status')
    else:
        if request.method == "POST":
            username = request.POST.get('username')
            password = request.POST.get('password')

            manager = authenticate(request, username=username, password=password)

            if manager is not None:
                login(request, manager)
                ThreadFarm([config.thermometer,config.fan,config.weightsensor,config.sonar], config.system).start_all()
                return redirect('status')
            else:
                messages.info(request, 'Username or Password is incorrect')
            

        return render(request, 'manager/login.html')

def logout_view(request):
    ThreadFarm([config.thermometer,config.fan,config.weightsensor,config.sonar], config.system).stop_all()
    logout(request)
    return redirect('login')

def registration_view(request):
    if request.user.is_authenticated:
        return redirect('status')
    else:

        form = ManagerRegistrationForm()

        if request.method == "POST":
            form = ManagerRegistrationForm(request.POST)
            if form.is_valid():
                form.save()
                manager = form.cleaned_data.get('username')
                messages.success(request, 'Account created for ' + manager)
                return redirect('login')

        context = {
            'form': form
        }
        return render(request, 'manager/registration.html', context)