# -Circles-2D-Graphics-handle touches-using-accelerometer

When the user places one finger on the screen a circle is drawn on the screen. 
The center ofthe circle is placed where the user touched the screen. 
The circle can be the color of your choice. The circle can be solid or not, again your choice. The longer the finger is pressed the
bigger the circle becomes. The user can put multiple circles on the screen by touching it multiple times. 

You should be able to support up to 15 different circles on the screen at a time.
The drawing area for the circles will be the entire screen. You can keep the title bar at the top of the screen if you like. 

The user can set a circle in motion by placing a finger on a circle and swiping in any direction.
The circles will start moving in direction and velocity indicated by the swipe. 
Different circles can be moving in different directions with different speeds.

Additional Features -:
1.When a circle reaches the edge of the screen it will bounce off the edge properly. That is it
will lose some velocity and take into account the angle it strikes the edge. For example if the
circle hits the edge at 90 degree angle it will bounce straight back. If it strikes the edge at a
45 degree angle it will bounce off at a 135 degree angle.
2. Have the circles bounce off each other in a reasonable way when they collide. This will require some physics as how two circle will react when they collide depends on angle of their
intersection, their velocities and their masses.
3. When circles are moving use the accelerometer to modify the direction and velocity of a circle. As the user tilts or moves the phone the circles in motion need to respond to changes in
the accelerometer. Keep in mind that the accelerometer give you acceleration not velocity.
The circle will be accelerated in the direction of the lower part of the device. If we tilt the top
of the device down the circle will accelerate toward the top of the device. If the tilt is slight
the acceleration is slight. If the circle is moving toward the bottom of the device when the
device is tilted so the top is lower than the bottom the circle does not immediately start moving toward the top of the device.
