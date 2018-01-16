package UI;

import ServoBlaster.ServoBlaster;
import com.pi4j.io.i2c.I2CFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Hashtable;

public class PiBlasterUI extends JFrame {
    public JPanel mainPanel;
    private JPanel dataPanel;
    private JPanel servoBlasterPanel;
    public JLabel setCurrent;
    public JLabel setVolt;
    private JSlider servoSlider;
    public JLabel setForce;
    public JLabel setTemperature;

    private Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();


    public PiBlasterUI() throws HeadlessException, IOException, I2CFactory.UnsupportedBusNumberException {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("GuPiBlaster");
        this.setBounds(0,0, screenSize.width, screenSize.height);
        this.setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        servoSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ServoBlaster servoBlaster = new ServoBlaster();
                try {
                    servoBlaster.setPWMSignal(servoSlider);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
