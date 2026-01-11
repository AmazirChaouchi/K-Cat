#!/usr/bin/python3

from time import sleep # DEBUG TODO: Remove before flight
from DoorSensor import DoorSensor

# Make these vars const
DOOR_SENSOR_PIN = 14

class LitterBoxManager:

    def __init__(self):
        # TODO: Initialize Pin Layout from file
        self.doorSensor = DoorSensor(DOOR_SENSOR_PIN)


    def setup(self):
        # TODO: Pairing
        # TODO: Calibration
        print("setup()")


    def run(self):
        # Main activity loop
        print("run()")

        previousDoorState = self.doorSensor.getStatus()

        while(True):
            currentDoorStatus = self.doorSensor.getStatus()
            if (currentDoorStatus == "open" and previousDoorStatus == "closed"):
                # TODO: timeCloseStart = timeCloseStop = null
                # TODO: Save weight1
                # TODO: Start 2" openTimer
                # TODO: Save timeOpenStart
                # TODO: Take care of the duration
                previousDoorStatus = currentDoorStatus
            elif (currentDoorStatus == "closed" and previousDoorStatus == "open"):
                # TODO: Save timeOpenStop
                # TODO: If timeOpenStop - timeOpenStart < 2", timeOpenStart = timeOpenStop = null
                # TODO: Start 2" closeTimer
                # TODO: Save timeCloseStart
            elif (currentDoorStatus == "closed" and previousDoorStatus == "closed"):
                # TODO: If now - timeCloseStart < 2", do nothing
                # TODO: Else, stop the closeTimer
                # TODO: Save weight2
                
if __name__ == "__main__":
    manager = LitterBoxManager()
    manager.setup()

    # Main program is located here
    manager.run()

