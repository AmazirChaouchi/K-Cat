#!/usr/bin/python3

# TEMP: LED
from gpiozero import LED
from time import time, localtime

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

        # TEMP: LED
        self.led = LED(LED_PIN)        


    def setup(self):
        # TODO: Pairing
        # TODO: Calibration
        self.weightSensor.setScale()
        print("setup()")


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

        while(True):
            currentDoorState = self.doorSensor.getStatus()
            if (currentDoorState == "open" and previousDoorState == "closed"):
                # TODO: timeCloseStart = timeCloseStop = None
                measuredWeight[0] = self.weightSensor.getWeight()
                # TODO: Start 2" openTimer
                measuredOpenTime[0] = time()
                # TODO: Take care of the duration
                previousDoorState = currentDoorState
            elif (currentDoorState == "closed" and previousDoorState == "open"):
                # TODO: Save timeOpenStop i.e. t1
                measuredOpenTime[1] = time()
                # If timeOpenStop - timeOpenStart < 2" then reset timers
                if (measuredOpenTime[1] - measuredOpenTime[0] < 2.0):
                    measuredOpenTime = [None, None]
                    measuredCloseTime[0] = time()
                    measuredWeight[1] = self.weightSensor.getWeight()
            elif (currentDoorState == "closed" and previousDoorState == "closed"):
                measuredCloseTime[1] = time()
                # TODO: If measuredCloseTime < 2", do nothing
                # TODO: Else, send information reagarding weight to the server
                measuredWeight[1] = self.weightSensor.getWeight()
                if (measuredCloseTime[1] - measuredCloseTime[0] > 2.0):
                    # Send a weight information to the server using the API
                    payload = {
                        "litiereId": "12345",
                        "poids": measuredWeight[1] - measuredWeight[0],
                        "timestamp": localtime()
                    }
                    response = requests.post("https://k-cat.onrender.com/api/litter-measurements", json=payload)
                    print(response.status_code) 
                    must_be_cleaned = response.json()   # True ou False
                    print("Doit être nettoyée ?", must_be_cleaned)
            else: # currentDoorState == "open" and previousDoorState = "open"
                pass

            if (must_be_cleaned):
                led.on()

                
if __name__ == "__main__":
    manager = LitterBoxManager()
    manager.setup()

    # Main program is located here
    manager.run()

