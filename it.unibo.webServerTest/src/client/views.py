from django import forms
from django.http import response
from django.shortcuts import render
from numpy import e
from .forms import PickupForm, SlotnumForm, CarenterForm
from manage import config
import socket
import re
import json
from django.contrib import messages

# Renders the webpage for the devices

def client_view(request):
    return render(request, 'client/index.html')

def get_json(msg):
    jsn_str = re.search("\{(.*?)\}", msg).group(0)
    return json.loads(jsn_str)

def notify_interest(request):

    

    slotnum = None
    error = None
    indoor = None
    form = SlotnumForm()
    email = None

    

    if request.method == 'POST':

        form = SlotnumForm(request.POST)

        if form.is_valid():
            name = form.cleaned_data['name']
            surname = form.cleaned_data['surname']
            email = form.cleaned_data['email']
            #slotnum = 5
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect((config.system.host, config.carparking.port))
            msg = f'msg(enter, request, python, {config.carparking.actor}, enter("{name}","{surname}","{email}"), 1)\n'
            print(msg)
            byt=msg.encode()   
            s.send(byt)


            recv_msg = ""
            while True:
                recv_msg += s.recv(1024).decode("utf-8")
                rec_end = recv_msg.find('\n')
                if rec_end != -1:
                    break

            s.close()
            print(recv_msg)
            recv_json = get_json(recv_msg)
            if "err" in recv_json:
                messages.error(recv_json["err"])
            else:
                flag = recv_json["indoor"].upper()
                indoor = 'The indoor is free you can enter the car!' if  flag == 'FREE' \
                        else 'The indoor is occupied.. an email will be sent to you when it is availlable!'
                slotnum = recv_json["slotnum"]
    else:
       
        if request.COOKIES.get('slotnum') != None:
            slotnum = request.COOKIES['slotnum']   


    context = {
        'slotnum': slotnum,
        'error': error,
        'indoor': indoor,
        'form': form
    }

    response = render(request, 'client/notify_interest.html', context)
    response.set_cookie('slotnum', context['slotnum'])
    response.set_cookie('email', email)
    return response

def carenter(request):

    slotnum = None
    email = None
    token = None
    form = CarenterForm()
    

    if request.method == 'POST':
        form = CarenterForm(request.POST)
        # check whether it's valid:
        if form.is_valid():
            slotnum = form.cleaned_data['slotnum']
            email = form.cleaned_data['email']
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect( (config.system.host, config.carparking.port))
            msg = f'msg(carenter, request, python, {config.carparking.actor}, carenter("{context["slotnum"]}","{context["email"]}"), 1)\n'
            print(msg)
            byt=msg.encode()   
            s.send(byt)

            
            recv_msg = ""
            while True:
                recv_msg += s.recv(1024).decode("utf-8")
                rec_end = recv_msg.find('\n')
                if rec_end != -1:
                    break

            s.close()
            recv_json = get_json(recv_msg)
            if "err" in recv_json:
                messages.error(recv_json["err"])
            else:
                token = recv_json["token"]

                
            
                    
    # if a GET (or any other method) we'll create a blank form
    else:
            if request.GET.get('slotnum') != "None":
                slotnum = request.GET.get('slotnum')
                email = request.GET.get('email')

            elif request.COOKIES.get('slotnum') != "None":
                slotnum = request.COOKIES['slotnum']
                email = request.COOKIES['email']
                
            if slotnum != None and email != None:
                form = CarenterForm(initial={'slotnum': slotnum, 'email': email})
                    

    context = {
        'slotnum': slotnum,
        'email': email,
        'token': token,
        'form': form
    }
    
    response = render(request, 'client/carenter.html', context)
    if token != None:
        response.delete_cookie('slotnum')
        response.delete_cookie('email')

    response.set_cookie('token', token)
    
    return response


def pickup(request):

    token =  None
    msg = None
    form = PickupForm()
    email = None
    


    
    if request.method == 'POST':
        form = PickupForm(request.POST)
        # check whether it's valid:
        if form.is_valid():
            token = form.cleaned_data['token']
            email = form.cleaned_data['email']
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect( (config.system.host, config.carparking.port))
            msg = f'msg(pickup, request, python, {config.carparking.actor}, pickup("{token}","{email}"), 1)\n'
            byt=msg.encode()   
            s.send(byt)
            
            recv_msg = ""
            while True:
                recv_msg += s.recv(1024).decode("utf-8")
                rec_end = recv_msg.find('\n')
                if rec_end != -1:
                    break

            s.close()
            print(recv_msg)
            recv_json = get_json(recv_msg)
            msg = recv_json["msg"]
            
                    
    # if a GET (or any other method) we'll create a blank form
    else:

        if request.GET.get('token') != "None":
            token = request.GET.get('token')
            email = request.GET.get('email')
        elif request.COOKIES.get('token') != "None":
            token = request.COOKIES['token']
            email = request.GET.get('email')
        
        if token != None and email != None:
            form = PickupForm(initial={'token': token,'email': email})

    context = {
        'token': token,
        'msg': msg,
        'form': form,
        'email': email
    }

    return render(request, 'client/pickup.html', context)

