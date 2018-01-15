import ADS.ADSReadVoltageCurrent;
import UI.PiBlasterUI;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

public class Driver{

    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException {

        PiBlasterUI ui = new PiBlasterUI();
        ADSReadVoltageCurrent readVoltageANDCurrent = new ADSReadVoltageCurrent();
        //TODO : uncomment lines of codes when ADS is setup for the memory address 0x49;
        /*Uncomment readForce when ADS has been setup properly. But this should work once uncommented
        ADSReadForce readForce = new ADSReadForce(); */

        readVoltageANDCurrent.start();
        ui.setContentPane(ui.mainPanel);
        while (true){
            ui.setVolt.setText(readVoltageANDCurrent.DF.format(readVoltageANDCurrent.getActualVoltage()) + " V");
            ui.setCurrent.setText(readVoltageANDCurrent.DF.format(readVoltageANDCurrent.getActualCurrent()) + " A");
        }

    }
}
