# MyVirtualGyroscope
Xposed module tested and working on API 25 or superior

This approach can handle the accelerometer vectors to create a virtual gyroscope sensor.

Changing the axis in proper way to emulate a head tracking device.

TODO:

 - Design a UI to show stats and vector values
 - Add more virtual sensor [Compass (Orientation), Temperature and Pressure]
 - Detect when non virtual sensor exist to take advantage of virtualization
 - Corrections to magnetic field calculations (add magnitude calc = sqrt(x*x+y*y+z*z))
 - Enable an TCP/UDP injection
 - Enable mouse to Virtual Sensor injection
 - Enable external Joystick to Virtual Sensor injection
 - Correct some bugs when interacts with the Xposed API
 - Apply well practices of developing
 
DONE:

 - Add more virtual sensor Compass (Orientation)
 - Correct some bugs when interacts with the Xposed API
 
