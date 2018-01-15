#This sets the PWM signal to be addressed at pin 12
gpio -g mode 18 pwm
#This sets the PWM to run at 50Hz
gpio pwm-ms
gpio pwmc 192
gpio pwmr 2000

#This runs the Java application
java -jar GuPiBlaster.jar
