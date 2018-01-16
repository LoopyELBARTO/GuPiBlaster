package Thermocouple;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.pi4j.wiringpi.Spi;


public class Thermocouple {

    public final DecimalFormat DF = new DecimalFormat("#.##");

    private int channel = Spi.CHANNEL_0;
    private int wiringPiSetup = Spi.wiringPiSPISetup(channel, 500000); //500 kHz

    private float thermocoupleTemp;
    private float internalTemp;

    private MAX31855 thermocoupleSensor = new MAX31855(channel);

    private static List<String> faults = new ArrayList<String>();

    public Thermocouple(){
        if (wiringPiSetup == -1) {
            throw new RuntimeException("SPI setup failed.");
        }
    }

    private static void onFaults(int f) {
        faults.clear();

        if ((f & MAX31855.FAULT_OPEN_CIRCUIT_BIT) == MAX31855.FAULT_OPEN_CIRCUIT_BIT)
            faults.add("Open Circuit");
        if ((f & MAX31855.FAULT_SHORT_TO_GND_BIT) == MAX31855.FAULT_SHORT_TO_GND_BIT)
            faults.add("Short To GND");
        if ((f & MAX31855.FAULT_SHORT_TO_VCC_BIT) == MAX31855.FAULT_SHORT_TO_VCC_BIT)
            faults.add("Short To VCC");

        boolean first = true;
        String text = "Faults = ";
        for (String fault : faults) {
            if (!first)
                text += ", ";
            text += fault;
        }

        System.err.println(text);
    }

    public void start() throws InterruptedException {

        int[] raw = new int[2];
            int faults = thermocoupleSensor.readRaw(raw);

            internalTemp = thermocoupleSensor.getInternalTemperature(raw[0]);
            thermocoupleTemp = thermocoupleSensor.getThermocoupleTemperature(raw[1]);

            //System.out.println("Internal = " + internalTemp + " C, Thermocouple = " + thermocoupleTemp + " C");
        System.out.println("Temp: " + thermocoupleTemp);
            if (faults != 0) {
                onFaults(faults);
            }
            Thread.sleep(500);
    }

    public double getThermocoupleTemp(){
        return this.thermocoupleTemp;
    }

}