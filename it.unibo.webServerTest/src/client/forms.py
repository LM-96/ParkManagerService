from django import forms

class SlotnumForm(forms.Form):
    name = forms.CharField(label='Name', max_length=100)
    surname = forms.CharField(label='Surname', max_length=100)
    email = forms.EmailField(label='Email', max_length=100)

class CarenterForm(forms.Form):
    slotnum = forms.CharField(label='Slotnum', max_length=100)
    email = forms.EmailField(label='Email', max_length=100)

class PickupForm(forms.Form):
    token = forms.CharField(label='Token', max_length=100)
