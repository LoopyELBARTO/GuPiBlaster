package ADS;

import com.pi4j.gpio.extension.ads.ADS1015GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.impl.PinImpl;

import java.util.EnumSet;

public class ADS1015DifferentialPins {

    public static final Pin INPUT_A0_A1 = createAnalogInputPin(0, "DIFF INPUT 0 AND 1");
    public static final Pin INPUT_A2_A3 = createAnalogInputPin(1, "DIFF INPUT 2 AND 3");

    private static Pin createAnalogInputPin(int address, String name) {
        return new PinImpl(ADS1015GpioProvider.NAME, address, name, EnumSet.of(PinMode.ANALOG_INPUT));
    }
}
