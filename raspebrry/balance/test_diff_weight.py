import time
import RPi.GPIO as GPIO
from hx711 import HX711

def cleanAndExit():
    print("Finishing")
    GPIO.cleanup()

# pins DT and  sck
DOUT = 5 # pin 29 GPIO 5
SCK = 6 # pin 31 GPIO 6
hx = HX711(DOUT, SCK)

hx.set_reading_format("MSB", "MSB")

referenceUnit = 470.1586
hx.set_reference_unit(referenceUnit)

# RAZ
hx.reset()
hx.tare()

print("Tare done!")

print ("Test to check the stability of the error")
print ("You will put the real weight when asked")

results = []
test_range = int(input("Number of object to test ? "))

for i in range (test_range):
    try:
        real_weight = float(input("\nPut the real weight of the object in gramme please : "))
        input("Put the weight and click on Enter")
        measured = hx.get_weight(5)
        results.append(real_weight - measured)

        input("Check, take the weight back and click on Enter")
        hx.power_down()
        time.sleep(0.1)
        hx.power_up()
        time.sleep(0.4)

    except (KeyboardInterrupt, SystemExit):
        cleanAndExit()

hx.power_down()
cleanAndExit()

print ("\n ----- Result ----- \n")
print ("Difference max : ", max(results) - min(results))
