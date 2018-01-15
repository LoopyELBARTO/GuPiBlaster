package ServoBlaster;

import javax.swing.*;
import java.io.IOException;

public class ServoBlaster {
    public void setPWMSignal(JSlider slider) throws IOException {
        Process process = Runtime.getRuntime().exec("gpio -g pwm 18 " + slider.getValue());
    }
}
