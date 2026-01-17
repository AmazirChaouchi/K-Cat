import time
import statistics
import RPi.GPIO as GPIO
from hx711 import HX711

def cleanAndExit():
    print("Closing...")
    GPIO.cleanup()
    print("Goodbye!")

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

print ("Stability test with no weight. Last 30s")

values = []
start = time.time()

while time.time() - start <30 :
    try:
        print("-", end="", flush=True)
        val = hx.get_weight(5)
        values.append(val)

        hx.power_down()
        time.sleep(0.1)
        hx.power_up()
        time.sleep(0.4)

    except (KeyboardInterrupt, SystemExit):
        cleanAndExit()

hx.power_down()
cleanAndExit()

print ("\n ----- Result ----- \n")
print ("Min : ", min(values))
print ("Max : ", max(values))
print ("Difference : ", max(values) - min(values))
