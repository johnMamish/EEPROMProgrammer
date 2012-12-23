package eepromprogrammer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import javax.comm.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.*;

/**
 *
 * @author John
 * A new iteration of this class is created whenever the user wants to
 * reconfigure the serial connection.
 */
public class ConnectionManagerWindow extends JFrame
{
    //inner classes and vars
    //<editor-fold>
    //variables
    //<editor-fold>
    //holds a refernece to the parent JFrame
    private JFrame parent;
    
    //holds a reference to this
    private JFrame thisJF = this;
    
    //JPanels to arrange layout
    JPanel comboBoxPanel;
    JPanel buttonsPanel;
    
    //insets...
    private final Insets stdInsets = new Insets(5, 5, 5, 5);
    
    //combo box options
    //<editor-fold>
    //contains all of the allowed baud rates
    int[] brates = {110, 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200};
    
    //contains all of the handshakes
    int[] handshakes = {
        SerialPort.FLOWCONTROL_NONE,
        SerialPort.FLOWCONTROL_RTSCTS_IN,
        SerialPort.FLOWCONTROL_RTSCTS_OUT,
        SerialPort.FLOWCONTROL_XONXOFF_IN,
        SerialPort.FLOWCONTROL_XONXOFF_OUT
    };
    
    String[] handshakeNames = {
        "No Flow Control",
        "RTS CTS In",
        "RTS CTS Out",
        "XONXOFF In",
        "XONXOFF Out"
    };
    //</editor-fold>
    
    //options JPanel and components
    //<editor-fold>
    private JPanel optionsPanel;
    private JLabel portLabel;
    private JComboBox portComboBox;
    private JLabel brateLabel;
    private JComboBox brateComboBox;
    private JLabel handshakeLabel;
    private JComboBox handshakeComboBox;
    
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
    //</editor-fold>
    //</editor-fold> 
    //inner classes
    //<editor-fold>
    //inner class that allows us to customize closing windows.
    class WindowCloser extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            parent.setFocusable(true);
            parent.setFocusableWindowState(true);
        }
    }
    
    //observer class for ok button
    class okActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            //TODO add code to set Serial Port options
            Window w = new Window(thisJF);
            w.getToolkit().getSystemEventQueue().postEvent(new WindowEvent(thisJF, WindowEvent.WINDOW_CLOSING));
        }
    }
    
    //workaround for combobox strings.
    class ComboBoxStringHolder
    {
        public String content;
        public ComboBoxStringHolder(String s)
        {
            this.content = s;
        }
        
        @Override
        public String toString()
        {
            return this.content;
        }
    }
    //</editor-fold>
    //</editor-fold>
    
    public ConnectionManagerWindow(JFrame parent)
    {
        //take care of business related to focusing parent
        //<editor-fold>
        this.parent = parent;
        this.parent.setFocusable(false);
        this.parent.setFocusableWindowState(false);
        this.toFront();
        
        //apply window closing action
        this.addWindowListener(new WindowCloser());
        //</editor-fold>
        
        //initialize labels, comboboxes, and buttons
        //<editor-fold>
        //initialize all labels
        //<editor-fold>
        portLabel = new JLabel("port");
        brateLabel = new JLabel("baud rate");
        handshakeLabel = new JLabel("handshake");
        //</editor-fold>
        
        //initialize all comboboxes
        //<editor-fold>
        portComboBox = new JComboBox();
        for(Enumeration e = CommPortIdentifier.getPortIdentifiers();e.hasMoreElements();)
        {
            CommPortIdentifier foo = (CommPortIdentifier)e.nextElement();
            System.out.println(foo.getName());
            portComboBox.addItem(new ComboBoxStringHolder(foo.getName()));
        }
        
        brateComboBox = new JComboBox();
        //add all baud reates to brate combo box
        for(Integer brate:brates)
        {
            brateComboBox.addItem(brate.toString());
        }
        
        handshakeComboBox = new JComboBox();
        //add all handshake names
        for(String s:handshakeNames)
        {
            handshakeComboBox.addItem(new ComboBoxStringHolder(s));
        }
        //</editor-fold>
        
        //initialize all buttons
        //<editor-fold>
        okButton = new JButton("ok");
        okButton.addActionListener(new okActionListener());
        
        cancelButton = new JButton("cancel");
        //cancelButton.addActionListener(new cancelActionListener());
        //</editor-fold>
        //</editor-fold>
        
        //layout code
        //<editor-fold>
        //layout for combo box JPanel
        //<editor-fold>
        comboBoxPanel = new JPanel();
        comboBoxPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), "connection options"));
        comboBoxPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = stdInsets;
        
        //port label
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
        comboBoxPanel.add(portLabel, c);
        
        //brate label
        c.gridy = 1;
        comboBoxPanel.add(brateLabel, c);
        
        //handshake label
        c.gridy = 2;
        comboBoxPanel.add(handshakeLabel, c);
        
        //port combo box
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 2;
        comboBoxPanel.add(portComboBox, c);
        
        //brate combo box
        c.gridy = 1;
        comboBoxPanel.add(brateComboBox, c);
        
        //handshake combo box
        c.gridy = 2;
        comboBoxPanel.add(handshakeComboBox, c);
        //</editor-fold>
        
        //layout for the buttons JPanel
        //<editor-fold>
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        buttonsPanel.add(okButton, c);
        
        c.gridy = 1;
        buttonsPanel.add(cancelButton, c);
        //</editor-fold>
        
        //adding JPanels to main JFrame
        //<editor-fold>
        this.setLayout(new GridBagLayout());
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        this.add(comboBoxPanel, c);
        
        c.gridx = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        this.add(buttonsPanel, c);
        
        //make size reasonable
        this.pack();
        this.setResizable(false);
        
        //</editor-fold>
        //</editor-fold>
    }
}
