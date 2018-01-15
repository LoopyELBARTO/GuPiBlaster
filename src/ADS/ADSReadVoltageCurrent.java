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

public class ADSReadVoltageCurrent {
    private static final double BASE_LINE = 0.5;
    private static final double OUTPUT_SENSITIVITY = 0.1333;

    private double value;
    private double percent;

    //VOLTAGE
    protected double rawVoltage;
    protected double actualVoltage;
    private final double multiplier = 4;

    //CURRENT
    private double actualCurrent;

    public GpioPinListener voltageANDCurrentListener;

    public final DecimalFormat DF = new DecimalFormat("#.##");

    public final GpioController GPIO = GpioFactory.getInstance();

    private final DifferentialGpioProvider DIFFERENTIAL_PROVIDER = new DifferentialGpioProvider(I2CBus.BUS_1, ADS1015GpioProvider.ADS1015_ADDRESS_0x48);

    public final GpioPinAnalog DIFF_ANALOG_INPUTS[] = {
            GPIO.provisionAnalogInputPin(DIFFERENTIAL_PROVIDER, ADS1015DifferentialPins.INPUT_A0_A1, "A0-A1")
    };


    public ADSReadVoltageCurrent() throws IOException, I2CFactory.UnsupportedBusNumberException {

    }
    public ADSReadVoltageCurrent(GpioPinAnalog gpioPinAnalog) throws IOException, I2CFactory.UnsupportedBusNumberException {


    }
    public void start(){
        setupGpio();
        analogPinValueListener();
        DIFF_ANALOG_INPUTS[0].addListener(voltageANDCurrentListener);
    }

    public void setupGpio() {
        DIFFERENTIAL_PROVIDER.setProgrammableGainAmplifier(
                ADS1x15GpioProvider.ProgrammableGainAmplifierValue.PGA_4_096V, ADS1015Pin.ALL);

        DIFFERENTIAL_PROVIDER.setEventThreshold(0.01, ADS1015Pin.ALL);

        DIFFERENTIAL_PROVIDER.setMonitorInterval(1000);
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
    }

    public void setListenerValue(GpioPinAnalogValueChangeEvent gpioEvent) {
        value = gpioEvent.getValue();
        percent = ((value * 100) / ADS1015GpioProvider.ADS1015_RANGE_MAX_VALUE);
        rawVoltage = DIFFERENTIAL_PROVIDER.getProgrammableGainAmplifier(gpioEvent.getPin()).getVoltage() * (percent/100);
        actualCurrent = (rawVoltage - BASE_LINE) / OUTPUT_SENSITIVITY;
        actualVoltage = rawVoltage * multiplier;
    }

    public double getActualVoltage() {
        return actualVoltage;
    }

    public double getActualCurrent() {
        return actualCurrent;
    }

    public void shutdown(){
        GPIO.isShutdown();
    }
}
