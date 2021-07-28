# from django.db import models
# from django.utils import timezone
# # Create your models here.
# class Client(models.Model):

#     STATE = (
#         ('Interested', 'Interested'),
#         ('Parked', 'Parked'),
#         ('Pickup', 'Pickup')
#     )

#     name = models.CharField(max_length=100)
#     surname = models.CharField(max_length=100)
#     email = models.EmailField(unique=True, max_length=100)
#     state = models.CharField(max_length=100, choices=STATE, default='Interested')
#     time = models.TimeField(default=timezone.now)

#     def __str__(self) -> str:
#         return f'Name: {self.name} Surname: {self.surname} Email: {self.email} State: {self.state}'


# class ParkingSlot(models.Model):
#     STATE = (
#         ('Free', 'Free'),
#         ('Reserved', 'Reserved'),
#         ('Occupied', 'Occupied')
#     )

#     slotstate = models.CharField(max_length=100, choices=STATE, default='Free')
#     token = models.CharField(max_length=100, unique=True)
#     client = models.ForeignKey(Client, on_delete=models.CASCADE)


#     def __str__(self) -> str:
#         return f'Slot state: {self.slotstate}'