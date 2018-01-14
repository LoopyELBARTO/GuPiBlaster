import ADS.ADSReadCurrent;
import ADS.ADSReadForce;
import ADS.ADSReadVoltage;
import UI.PiBlasterUI;
import com.pi4j.io.i2c.I2CFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Driver{

    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException {
        boolean isEndButtonPressed = false;

        PiBlasterUI ui = new PiBlasterUI();

        //TODO : uncomment lines of codes when ADS is setup for the memory address 0x49;
        /*Uncomment readForce when ADS has been setup properly. But this should work once uncommented
        ADSReadForce readForce = new ADSReadForce(); */

        ui.setContentPane(ui.mainPanel);

    }
}
