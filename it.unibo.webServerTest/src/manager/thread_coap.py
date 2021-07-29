from re import S
from threading import Thread
from typing import List
import time
import websockets
import socket
import json

class Singleton(type):
    _instances = {}
    def __call__(cls, *args, **kwargs):
        if cls not in cls._instances:
            cls._instances[cls] = super(Singleton, cls).__call__(*args, **kwargs)
        return cls._instances[cls]

class ThreadCoap(Thread):

    def __init__(self, actor, system):
        self.kill = False
        self.actor = actor
        self.system = system
        super(ThreadCoap, self).__init__()

    def stop(self):
        self.kill = True
        self.s.close()
        

    async def run(self):
        print("RUN "+ self.actor.name)
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s.connect( (self.system.host, self.actor.port))
        while not self.kill:
            recv_msg = ""
            while True:
                recv_msg += self.s.recv(1024).decode("utf-8")
                rec_end = recv_msg.find('\n')
                if rec_end != -1:
                    async with websockets.connect("SOME WEBSOCKET") as websocket:
                        await websocket.send(json.dumps({'data': recv_msg}))
                    break

            print("Payload " + self.actor.name + ": " + recv_msg)
            time.sleep(4)
        
        print("KILL "+ self.actor.name)

class ThreadFarm(metaclass=Singleton):
    def __init__(self, actors, system):
        self.actors = actors
        self.system = system


    def start_all(self):
        self.threads = []
        for actor in self.actors:
            th = ThreadCoap(actor, self.system)
            th.start()
            self.threads.append(th)

    def stop_all(self):
        for th in self.threads:
            th.stop()

