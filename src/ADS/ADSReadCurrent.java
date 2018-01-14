package ADS;

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
import java.text.DecimalFormat;

public class ADSReadCurrent {
    private static final double BASE_LINE = 0.5;
    private static final double OUTPUT_SENSITIVITY = 0.133;
    private double value;
    private double percent;
    private double rawVoltage;
    private double current;

    public GpioPinListener currentListener;

    public  final DecimalFormat DF = new DecimalFormat("#.##");

    private final GpioController GPIO = GpioFactory.getInstance();

    private final DifferentialGpioProvider DIFFERENTIAL_PROVIDER = new DifferentialGpioProvider(
            I2CBus.BUS_1, ADS1015GpioProvider.ADS1015_ADDRESS_0x48);

    protected final GpioPinAnalog DIFF_ANALOG_INPUTS[] = {
            GPIO.provisionAnalogInputPin(DIFFERENTIAL_PROVIDER, ADS1015DifferentialPins.INPUT_A0_A1)
    };

    public ADSReadCurrent() throws IOException, I2CFactory.UnsupportedBusNumberException {
        setupGpio();
        analogPinValueListener();
        DIFF_ANALOG_INPUTS[0].addListener(currentListener);
    }

    public void setupGpio() {
        DIFFERENTIAL_PROVIDER.setProgrammableGainAmplifier(
                ADS1x15GpioProvider.ProgrammableGainAmplifierValue.PGA_4_096V, ADS1015Pin.ALL);
        DIFFERENTIAL_PROVIDER.setEventThreshold(0.01, ADS1015Pin.ALL);

        DIFFERENTIAL_PROVIDER.setMonitorInterval(1000);
    }

    public void analogPinValueListener() {
        currentListener = new GpioPinListenerAnalog() {
            @Override
            public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
                setListenerValue(event);
                System.out.println("amp: " + DF.format(getAcutalCurrent()));
            }
        };
    }

    public void setListenerValue(GpioPinAnalogValueChangeEvent gpioEvent) {
        value = gpioEvent.getValue();
        percent = ((value * 100) / ADS1015GpioProvider.ADS1015_RANGE_MAX_VALUE);
        rawVoltage = DIFFERENTIAL_PROVIDER.getProgrammableGainAmplifier(gpioEvent.getPin()).getVoltage();
        current = (rawVoltage - BASE_LINE) /OUTPUT_SENSITIVITY;
    }

    public double getAcutalCurrent() {
        return current;
    }
}
