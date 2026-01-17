import time
import sys
import RPi.GPIO as GPIO
from hx711 import HX711

def cleanAndExit():
    print("Closing...")
    GPIO.cleanup()    
    print("Goodbye!")
    sys.exit()

# pins DT and  sck
DOUT = 5 # pin 29 GPIO 5
SCK = 6 # pin 31 GPIO 6
hx = HX711(DOUT, SCK)

hx.set_reading_format("MSB", "MSB")

'''
# HOW TO CALCULATE THE REFFERENCE UNIT
1. Set the reference unit to 1 and launch the .py with nothing on it
2. Put a 1000g load on it (name poids later)
3. Write down the value you're getting. Make sure it's consistent (if needed do a mean)
4. referenceUnit = value / poids
'''
referenceUnit = 470.1586
hx.set_reference_unit(referenceUnit)

# RAZ
hx.reset()
hx.tare()

print("Tare done!")

while True:
    try:
        # Prints the weight in gramme
        val = max(0, hx.get_weight(5)) # we can't have negative weight
        print(val)

        # in order to keep stability, less noise
        hx.power_down()
        time.sleep(0.1)
        hx.power_up()
        time.sleep(0.4)

    except (KeyboardInterrupt, SystemExit):
        cleanAndExit()
