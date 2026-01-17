from hx711 import HX711

class WeightSensor:

    def __init__(self, doutPinNumber, sckPinNumber):
        # preparation of the amplificator
        self.hx = HX711(doutPinNumber, sckPinNumber)
        self.hx.set_reading_format("MSB", "MSB")
        # value tested and choose before-hand with some test
        self.referenceUnit = 470.1586
        self.hx.set_reference_unit(self.referenceUnit)
        self.hx.reset()
        self.hx.tare()


    def setScale(self):
        self.hx.reset()
        self.hx.tare()


    def getWeight(self):
        # weight is in gramme
        return self.hx.get_weight(5)

