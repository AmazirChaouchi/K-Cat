#!/usr/bin/python3

# TEMP: LED
from gpiozero import LED
from time import time, localtime, sleep, strftime
import requests

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


    def setup(self):
        print("setup")
        self.led.on()
        # TODO: Pairing
        self.weightSensor.setScale()
        sleep(1)
        self.led.off()


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
                    # RAZ weight to not seend infinitly
                    measuredWeight[0] = measuredWeight[1] = None
            else: # currentDoorState == "open" and previousDoorState = "open"
                # print("open -> open")
                pass

            if (must_be_cleaned):
                self.led.on()

            previousDoorState = currentDoorState
            if (measuredWeight[0] != None and measuredWeight[1] != None):
                print(f"[DEBUG]: weight = {measuredWeight[1] - measuredWeight[0]}")
            sleep(0.5)

                
if __name__ == "__main__":
    manager = LitterBoxManager()
    manager.setup()

    # Main program is located here
    manager.run()

