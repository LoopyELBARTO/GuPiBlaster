import ADS.ADSReader;
import Thermocouple.Thermocouple;
import UI.PiBlasterUI;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

public class Driver{

    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {

        PiBlasterUI ui = new PiBlasterUI();
        ADSReader adsReader = new ADSReader();
        Thermocouple readTemp = new Thermocouple();
        //TODO : uncomment lines of codes for Force when ADS is setup for the memory address 0x49;


        ui.setContentPane(ui.mainPanel);
        adsReader.start();

        while (true){
            ui.setVolt.setText(adsReader.DF.format(adsReader.getActualVoltage()) + " V");
            ui.setCurrent.setText(adsReader.DF.format(adsReader.getActualCurrent()) + " A");
            //ui.setForce.setText(adsReader.DF.format(adsReader.getActualForce()));
            readTemp.start();
            ui.setTemperature.setText(readTemp.DF.format(readTemp.getThermocoupleTemp()) + " C");
        }

    }
}
