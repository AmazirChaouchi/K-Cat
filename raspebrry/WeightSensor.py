from hx711 import HX711

class WeightSensor:

    def __init__(self, doutPinNumber, sckPinNumber):
        self.hx = HX711(doutPinNumber, sckPinNumber)
        hx.set_reading_format("MSB", "MSB")
        self.referenceUnit = 470.1586
        hx.set_reference_unit(referenceUnit)
        hx.reset()
        hx.tare()


    def getWeight():
        return hx.get_weight(5)

