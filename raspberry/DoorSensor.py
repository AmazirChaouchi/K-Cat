from gpiozero import Button

class DoorSensor:

    def __init__(self, pinNumber):
        self.button = Button(pinNumber)
    
    def getStatus(self):
        if (self.button.is_pressed):
            return "closed"
        else:
            return "open"

