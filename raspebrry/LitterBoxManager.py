#!/usr/bin/python3

# TEMP: LED
from gpiozero import LED
from time import time, localtime, sleep, strftime
import requests
import threading

from DoorSensor import DoorSensor
from WeightSensor import WeightSensor

# Make these vars const
DOOR_SENSOR_PIN = 14
WEIGHT_DOUT_PIN = 5
WEIGHT_SCK_PIN = 6
LED_PIN = 26

class LitterBoxManager:

    def __init__(self):
        # TODO: Initialize Pin Layout from file
        self.doorSensor = DoorSensor(DOOR_SENSOR_PIN)
        self.weightSensor = WeightSensor(WEIGHT_DOUT_PIN, WEIGHT_SCK_PIN)
        self.led = LED(LED_PIN)
        self.led_thread = None
        self.led_stop_event = threading.Event()
     


    def setup(self):
        print("setup")
        self.led.on()
        # TODO: Pairing
        self.weightSensor.setScale()
        sleep(1)
        self.led.off()

    # Requete faisant un get a l'api pour verifier si la litiere doit etre nettoyee
    def must_be_cleaned(self):
        r = requests.get(API_URL, params={"litiereId": LITIERE_ID}, timeout=5)

        if r.status_code != 200:
            print("Erreur API :", r.status_code)
            return False

        data = r.json()
        return data.get("shouldBeCleanedUp", False)


    # Gestion de la LED : une fois allumée, on fait une requête vers l'API toutes les 
    # dix secondes pour savoir quand l'éteindre (methode lancée dans un thread)
    def led_monitor(self, stop_event):
        self.led.on()
        print("LED allumée")

        # boucle qui check toutes les 10 secondes si il faut eteindre la LED
        while not stop_event.is_set():
            if not self.must_be_cleaned():
                break
            sleep(10)

        self.led.off()
        print("LED éteinte")


    def run(self):
        # Main activity loop
        print("run()")

        previousDoorState = self.doorSensor.getStatus()
        # measuredWeight[0]: litter box is empty
        # measuredWeight[1]: there is a cat in the litter box
        # measuredWeight[1] - measuredWeight[0] = cat's weight
        measuredWeight = [None, None]
        # measureTime[0] = start time i.e. t0
        # measureTime[1] = stop time i.e. t1
        measuredOpenTime = [None, None]
        measuredCloseTime = [None, None]
        must_be_cleaned = False

        while(True):
            currentDoorState = self.doorSensor.getStatus()
            if (currentDoorState == "open" and previousDoorState == "closed"):
                # print("closed -> open")
                measuredWeight[0] = self.weightSensor.getWeight()
                measuredOpenTime[0] = time()
            elif (currentDoorState == "closed" and previousDoorState == "open"):
                # print("open -> closed")
                measuredOpenTime[1] = time()
                measuredCloseTime[0] = time()
                measuredWeight[1] = self.weightSensor.getWeight()
                # If timeOpenStop - timeOpenStart < 2" then reset timers
                if (measuredOpenTime[1] - measuredOpenTime[0] < 2.0):
                    measuredOpenTime = [None, None]
            elif (currentDoorState == "closed" and previousDoorState == "closed"):
                # print("closed -> closed")
                measuredCloseTime[1] = time()
                # If measuredCloseTime < 2", do nothing
                # Else, send information reagarding weight to the server
                if (measuredWeight[0] != None and measuredWeight[1] != None and (measuredWeight[1] > measuredWeight[0] + 20) and measuredCloseTime[1] - measuredCloseTime[0] > 2.0):
                # if (measuredCloseTime[1] - measuredCloseTime[0] > 2.0):
                    # Send a weight information to the server using the API
                    payload = {
                        "litiereId": "1318",
                        "poids": measuredWeight[1] - measuredWeight[0],
                        "timestamp": strftime("%Y-%m-%dT%H:%M:%S", localtime())
                    }
                    response = requests.post("https://k-cat.onrender.com/api/litter-measurements", json=payload)
                    print(response.json())
                    must_be_cleaned = response.json()   # True ou False
                    print("Doit être nettoyée ?", must_be_cleaned)
                    if must_be_cleaned:
                        if self.led_thread is None or not self.led_thread.is_alive():
                            self.led_stop_event.clear()
                            self.led_thread = threading.Thread(
                                target=self.led_monitor,
                                args=(self.led_stop_event,),
                                daemon=True
                            )
                            self.led_thread.start()

                    if not must_be_cleaned and self.led_thread and self.led_thread.is_alive():
                        self.led_stop_event.set()

                    
                    # RAZ weight to not seend infinitly
                    measuredWeight[0] = measuredWeight[1] = None

            else: # currentDoorState == "open" and previousDoorState = "open"
                # print("open -> open")
                pass


            previousDoorState = currentDoorState
            if (measuredWeight[0] != None and measuredWeight[1] != None):
                print(f"[DEBUG]: weight = {measuredWeight[1] - measuredWeight[0]}")
            sleep(0.5)

                
if __name__ == "__main__":
    manager = LitterBoxManager()
    manager.setup()

    # Main program is located here
    manager.run()

