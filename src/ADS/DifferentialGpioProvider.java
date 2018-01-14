package ADS;

import com.pi4j.gpio.extension.ads.ADS1015GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

public class DifferentialGpioProvider extends ADS1015GpioProvider {
    public int config;
    public DifferentialGpioProvider(int busNumber, int address) throws I2CFactory.UnsupportedBusNumberException, IOException {
        super(busNumber, address);
    }
    public double getImmediateValue(Pin pin) throws IOException{
         config = ADS1x15_REG_CONFIG_CQUE_NONE    | // Disable the comparator (default val)
                ADS1x15_REG_CONFIG_CLAT_NONLAT  | // Non-latching (default val)
                ADS1x15_REG_CONFIG_CPOL_ACTVLOW | // Alert/Rdy active low   (default val)
                ADS1x15_REG_CONFIG_CMODE_TRAD   | // Traditional comparator (default val)
                ADS1x15_REG_CONFIG_DR_1600SPS   | // 1600 samples per second (default)
                ADS1x15_REG_CONFIG_MODE_SINGLE;    // Single-shot mode (default)

        config |= pga[pin.getAddress()].getConfigValue();

        //the commented switch statements are for single ended reading of voltage
        switch (pin.getAddress()){
            /*case (0):
                config |= ADS1x15_REG_CONFIG_MUX_SINGLE_0;
                break;
            case (1):
                config |= ADS1x15_REG_CONFIG_MUX_SINGLE_1;
                break;
            case(2):
                config |= ADS1x15_REG_CONFIG_MUX_SINGLE_2;
                break;
            case (3):
                config |= ADS1x15_REG_CONFIG_MUX_SINGLE_3;
                break;*/
            case (0):
                config |= ADS1x15_REG_CONFIG_MUX_DIFF_0_1;
                break;
            case (1):
                config |= ADS1x15_REG_CONFIG_MUX_DIFF_2_3;
        }
        config |= ADS1x15_REG_CONFIG_OS_MASK;
        // Write config register to the ADC
        writeRegister(ADS1x15_REG_POINTER_CONFIG, config);

        // Wait for the conversion to complete
        try{
            if(conversionDelay > 0){
                Thread.sleep(conversionDelay);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // read the conversion results
        int value = readRegister(ADS1x15_REG_POINTER_CONVERT);

        getPinCache(pin).setAnalogValue(value);
        return value;
    }
}
