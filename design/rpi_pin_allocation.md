# Raspberry Pi Pin Allocation Layout

This document gathers information regarding the allocation of pins on the RPi used by this project.

>>>
**Important**
For the sake of consistency, this file should be kept updated whenever a change is made.
>>>

Reminder: Output of the `pinout` command:

```
   3V3  (1) (2)  5V
 GPIO2  (3) (4)  5V
 GPIO3  (5) (6)  GND
 GPIO4  (7) (8)  GPIO14
   GND  (9) (10) GPIO15
GPIO17 (11) (12) GPIO18
GPIO27 (13) (14) GND
GPIO22 (15) (16) GPIO23
   3V3 (17) (18) GPIO24
GPIO10 (19) (20) GND
 GPIO9 (21) (22) GPIO25
GPIO11 (23) (24) GPIO8
   GND (25) (26) GPIO7
 GPIO0 (27) (28) GPIO1
 GPIO5 (29) (30) GND
 GPIO6 (31) (32) GPIO12
GPIO13 (33) (34) GND
GPIO19 (35) (36) GPIO16
GPIO26 (37) (38) GPIO20
   GND (39) (40) GPIO21
```

| Pin | Value | Connected| Pin | Value | Connected|
| - | - | - | - | - | - |
| 1 | 3V3 | `WeightSensor::vcc` | 2 | 5V | - |
| 3 | GPIO2 | - | 4 | 5V | - |
| 5 | GPIO3 | - | 6 | GND | `DoorSensorHandler::Button` |
| 7 | GPIO4 | - | 8 | GPIO14 | `DoorSensorHandler::Button` |
| 9 | GND | `WeightSensor::GND` | 10 | GPIO15 | - |
| 11 | GPIO17 | - | 12 | GPIO18 | - |
| 13 | GPIO27 | - | 14 | GND | - |
| 15 | GPIO22 | - | 16 | GPIO23 | - |
| 17 | 3V3 | - | 18 | GPIO24 | - |
| 19 | GPIO10 | - | 20 | GND | - |
| 21 | GPIO9 | - | 22 | GPIO25 | - |
| 23 | GPIO11 | - | 24 | GPIO8 | - |
| 25 | GND | - | 26 | GPIO7 | - |
| 27 | GPIO0 | - | 28 | GPIO1 | - |
| 29 | GPIO5 | `WeightSensor::dt` | 30 | GND | - |
| 31 | GPIO6 | `WeightSensor::sck` | 32 | GPIO12 | - |
| 33 | GPIO19 | - | 34 | GND | - |
| 35 | GPIO19 | - | 36 | GPIO16 | - |
| 37 | GPIO26 | `Ledsensor::GPIO` | 38 | GPIO20 | - |
| 39 | GND | `LEDsensor::GND` | 40 | GPIO21 | - |

Special thanks to `fxadecimal` for making [this template](https://gist.github.com/fxadecimal/7bf8d228e8b229d78fa9e63a146b9d3e) available.
