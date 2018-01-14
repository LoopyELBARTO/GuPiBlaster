package UI;

import ADS.ADSReadCurrent;
import ADS.ADSReadVoltage;
import com.pi4j.io.i2c.I2CFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Stack;

public class PiBlasterUI extends JFrame {
    public JPanel mainPanel;
    private JPanel dataPanel;
    private JPanel servoBlasterPanel;
    private JPanel menuBarPanel;
    private JTabbedPane tabbedPane;
    private JPanel setupTab;
    public JLabel setForce;
    public JLabel setCurrent;
    public JLabel setVolt;
    private JSlider servoSlider;
    private JButton openServoBlasterDirectory;
    private JRadioButton startDataReadingRadioButton;
    private JRadioButton endRadioButton;

    private ADSReadVoltage readVoltage = new ADSReadVoltage();
    private ADSReadVoltage voltA = readVoltage;
    private ADSReadCurrent readCurrent;

    public PiBlasterUI() throws HeadlessException, IOException, I2CFactory.UnsupportedBusNumberException {
        this.setTitle("GuPiBlaster");
        this.setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        startDataReadingRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("starting readings from ADS");
                voltA = readVoltage;
                voltA.Start();
            }
        });
        endRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("end readings from ADS");
                readVoltage.GPIO.shutdown();
                voltA = null;
                System.gc();
            }
        });
    }

}
