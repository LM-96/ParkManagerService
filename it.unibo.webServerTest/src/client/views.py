from django import forms
from django.http import response
from django.shortcuts import render
from numpy import e
from .forms import PickupForm, SlotnumForm, CarenterForm
from manage import config
import socket
import re
import json

# Renders the webpage for the devices

def client_view(request):
    return render(request, 'client/index.html')

def get_json(msg):
    jsn_str = re.search("\[(.*?)\]", msg)
    return json.loads(jsn_str)

def notify_interest(request):

    context = {
        'slotnum': None,
        'error': None,
        'indoor': None,
        'form': None
    }

    email = None

    if request.COOKIES.get('slotnum'):
        context['slotnum'] = request.COOKIES['slotnum']

    # if this is a POST request we need to process the form data
    if request.method == 'POST':
        # create a form instance and populate it with data from the request:
        context['form'] = SlotnumForm(request.POST)
        # check whether it's valid:
        if context['form'].is_valid():
            name = context['form'].cleaned_data['name']
            surname = context['form'].cleaned_data['surname']
            email = context['form'].cleaned_data['email']
            #slotnum = 5
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect( (config.system.host, config.carparking.port))
            msg = f'msg(enter, request, python, {config.carparking.actor}, enter({name},{surname},{email}), 1)\n'
            print(msg)
            byt=msg.encode()   
            s.send(byt)

            #json  {'slotnum', 'err', 'indoor'} parse with regex

            recv_msg = s.recv(1024)
            s.close()
            recv_json = get_json(recv_msg)
            if recv_json["err"] != None:
                context['error'] = recv_json["err"]
            else:
                flag = recv_json["indoor"]
                context['indoor'] = 'The indoor is free you can enter the car!' if  flag == 'Free' \
                        else 'The indoor is occupied.. an email will be sent to you when it is availlable!'
                context['slotnum'] = recv_json["slotnum"]
                    
    # if a GET (or any other method) we'll create a blank form
    else:
        context['form'] = SlotnumForm()

    response = render(request, 'client/notify_interest.html', context)
    response.set_cookie('slotnum', context['slotnum'])
    response.set_cookie('email', email)
    return response

def carenter(request):

    context = {
        'slotnum': None,
        'email': None,
        'token': None,
        'error': None,
        'form': None
    }

    if request.GET.get('slotnum') != None:
        context['slotnum'] = request.GET.get('slotnum')
        context['email'] = request.GET.get('email')

    elif request.COOKIES.get('slotnum'):
        context['slotnum'] = request.COOKIES['slotnum']
        context['email'] = request.COOKIES['email']

    if request.method == 'POST':
        context['form'] = CarenterForm(request.POST)
        # check whether it's valid:
        if context['form'].is_valid():
            context['slotnum'] = context['form'].cleaned_data['slotnum']
            context['email'] = context['form'].cleaned_data['email']
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect( (config.system.host, config.carparking.port))
            msg = f'msg(carenter, request, python, {config.carparking.actor}, carenter({context["slotnum"]}, {context["email"]}), 1)\n'
            byt=msg.encode()   
            s.send(byt)

            # json {'token', 'err'}
            
            recv_msg = s.recv(1024)
            s.close()
            recv_json = get_json(recv_msg)
            if recv_json["err"] != None:
                context['error'] = recv_json["err"]
            else:
                context['token'] = recv_json["token"]
            
                    
    # if a GET (or any other method) we'll create a blank form
    else:
        
        if context['slotnum'] != None:
            context['form'] = CarenterForm(initial={'slotnum': context['slotnum'], 'email': context['email']})
        else:
            context['form'] = CarenterForm()

    return render(request, 'client/carenter.html', context)


def pickup(request):
    
    context = {
        'token': None,
        'msg': None,
        'form': None
    }

    
    if request.method == 'POST':
        context['form'] = PickupForm(request.POST)
        # check whether it's valid:
        if context['form'].is_valid():
            context['token'] = context['form'].cleaned_data['token']
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect( (config.system.host, config.carparking.port))
            msg = f'msg(pickup, request, python, {config.carparking.actor}, pickup({context["token"]}), 1)\n'
            byt=msg.encode()   
            s.send(byt)
            
            recv_msg = s.recv(1024)
            s.close()
            recv_json = get_json(recv_msg)
            context['msg'] = recv_json["msg"]
            
                    
    # if a GET (or any other method) we'll create a blank form
    else:

        if request.GET.get('token') != None:
            context['token'] = request.GET.get('token')
        elif request.COOKIES.get('token'):
            context['token'] = request.COOKIES['token']

        
        if context['token'] != None:
            context['form'] = PickupForm(initial={'token': context['token']})
        else:
            context['form'] = PickupForm()

    return render(request, 'client/pickup.html', context)

