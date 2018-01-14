package ServoBlaster;

import Interfaces.ServoBlasterInterface;
import Interfaces.setValueInterface;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class MainServoBlaster implements ServoBlasterInterface, setValueInterface {
  
    /*
     servodDirectory does not acurately tell someone what "dicrectory" it is.
     For someone without knowledge of the system, servod could refer to many different things.
     An example of a more descriptive name would be "ServoBlasterDirectory" or "ProgramLocation".
     */

    private String servodFileDirectory = "/home/pi/PiBits/ServoBlaster/user";
    private String devFileServoBlasterDirectory  = "/dev/servoblaster";

    private File blasterDirectory = new File(devFileServoBlasterDirectory);
    private PrintWriter commandToServod;
  
    private ProcessBuilder startServodShellFile = new ProcessBuilder("/bin/sh", "launchServod.sh");
    private ProcessBuilder quitServodShellFile = new ProcessBuilder("/bin/sh", "quitServod.sh");
    private Process process;
  
  
    /*
     In regards to making things modular for resuse in the future.
     The following, while good named methods for this project; could be made more naming genetic.
     This avoids the copying of code with different names, for other possible use cases.
     An example of a more generic name would be "startProgram" additionally,
     servodDirectory could be changed to "progeamDirectory" etc as mentioned above.
     */
  
    public void startProgram() throws IOException {
        startServodShellFile.directory(new File(servodFileDirectory));
        process = startServodShellFile.start();
    }

    public void endProgram() throws IOException {
        quitServodShellFile.directory(new File(servodFileDirectory));
        process =  quitServodShellFile.start();
    }

    public void setSliderValue(int sliderValue) throws InterruptedException {
        try {
            commandToServod = new PrintWriter(blasterDirectory);
            commandToServod.println("2=" + sliderValue + "us");
            commandToServod.close();
        }catch (IOException e){
            System.out.println("Connot find /dev/servoblaster");
        }
    }

}
