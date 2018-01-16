package ADS;

import UI.PiBlasterUI;
import com.pi4j.gpio.extension.ads.ADS1015GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1015Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalog;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.sql.Driver;
import java.text.DecimalFormat;

public class ADSReader {
    private static final int MONITOR_INTERVAL = 1000;
    private static final double EVENT_THRESHOLD = 0.01;

    //Current
    private static final double BASE_LINE_CURRENT = 0.5;
    private static final double OUTPUT_SENSITIVITY_CURRENT = 0.1333;

    //FORCE
    private static final double BASE_LINE_FORCE = 12.25;
    private static final double OUTPUT_SENSITIVITY_FORCE = -4.9;

    private double value;
    private double percent;

    //VOLTAGE
    protected double rawVoltage;
    protected double actualVoltage;
    private final double multiplier = 4;

    //CURRENT
    private double actualCurrent;

    //FORCE
    private double actualForce;

    public GpioPinListener voltageANDCurrentListener;
    public GpioPinListener forceListener;

    public final DecimalFormat DF = new DecimalFormat("#.##");

    public final GpioController GPIO = GpioFactory.getInstance();

    //Voltage and Current Differential Provider
    private final DifferentialGpioProvider DIFFERENTIAL_PROVIDER_V_C = new DifferentialGpioProvider(I2CBus.BUS_1, ADS1015GpioProvider.ADS1015_ADDRESS_0x48);

    //Force Differential Provider
    //private final DifferentialGpioProvider DIFFERENTIAL_PROVIDER_F = new DifferentialGpioProvider(I2CBus.BUS_1, ADS1015GpioProvider.ADS1015_ADDRESS_0x49);

    public final GpioPinAnalog DIFF_ANALOG_INPUTS[] = {
            GPIO.provisionAnalogInputPin(DIFFERENTIAL_PROVIDER_V_C, ADS1015DifferentialPins.INPUT_A0_A1, "A0-A1")
            //GPIO.provisionAnalogInputPin(DIFFERENTIAL_PROVIDER_F, ADS1015DifferentialPins.INPUT_A0_A1, "A0-A1")
    };


    public ADSReader() throws IOException, I2CFactory.UnsupportedBusNumberException {

    }
    public ADSReader(GpioPinAnalog gpioPinAnalog) throws IOException, I2CFactory.UnsupportedBusNumberException {


    }
    public void start(){
        setupGpio();
        analogPinValueListener();
        DIFF_ANALOG_INPUTS[0].addListener(voltageANDCurrentListener);
        //DIFF_ANALOG_INPUTS[1].addListener(forceListener);
    }

    public void setupGpio() {
        //VOLTAGE AND CURRENT SETUP
        DIFFERENTIAL_PROVIDER_V_C.setProgrammableGainAmplifier(
                ADS1x15GpioProvider.ProgrammableGainAmplifierValue.PGA_4_096V, ADS1015Pin.ALL);

        DIFFERENTIAL_PROVIDER_V_C.setEventThreshold(EVENT_THRESHOLD, ADS1015Pin.ALL);

        DIFFERENTIAL_PROVIDER_V_C.setMonitorInterval(MONITOR_INTERVAL);

        //FORCE SETUP
        /*DIFFERENTIAL_PROVIDER_F.setProgrammableGainAmplifier(
                ADS1x15GpioProvider.ProgrammableGainAmplifierValue.PGA_4_096V, ADS1015Pin.ALL);
        DIFFERENTIAL_PROVIDER_F.setEventThreshold(EVENT_THRESHOLD, ADS1015Pin.ALL);
        DIFFERENTIAL_PROVIDER_F.setMonitorInterval(MONITOR_INTERVAL);*/
    }

    public void analogPinValueListener() {
        voltageANDCurrentListener = new GpioPinListenerAnalog() {
            @Override
            public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
                setListenerValue(event);
                System.out.println("Volt: " + DF.format(getActualVoltage()));
                System.out.println("Amp: " + DF.format(getActualCurrent()));
            }
        };
        /*forceListener = new GpioPinListenerAnalog() {
            @Override
            public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent gpioPinAnalogValueChangeEvent) {
                setListenerValue(gpioPinAnalogValueChangeEvent);
                System.out.println("Newton: " + DF.format(getActualForce()));
            }
        };*/
    }

    public void setListenerValue(GpioPinAnalogValueChangeEvent gpioEvent) {
        //RAW VALUES
        value = gpioEvent.getValue();
        percent = ((value * 100) / ADS1015GpioProvider.ADS1015_RANGE_MAX_VALUE);
        rawVoltage = DIFFERENTIAL_PROVIDER_V_C.getProgrammableGainAmplifier(gpioEvent.getPin()).getVoltage() * (percent/100);

        //ACTUAL VALUES
        //actualForce = (rawVoltage * OUTPUT_SENSITIVITY_FORCE) + BASE_LINE_FORCE;
        actualCurrent = (rawVoltage - BASE_LINE_CURRENT) / OUTPUT_SENSITIVITY_CURRENT;
        actualVoltage = rawVoltage * multiplier;
    }

    public double getActualVoltage() {
        return actualVoltage;
    }

    public double getActualCurrent() {
        return actualCurrent;
    }

    /*public double getActualForce() {
        return actualForce;
    }*/

    public void shutdown(){
        GPIO.isShutdown();
    }
}
