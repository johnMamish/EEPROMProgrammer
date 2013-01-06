/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eepromprogrammer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author John
 */
public class MainWindow extends JFrame
{
    //inner classes
    //<editor-fold>
    class ConnectionOptionsListener implements ActionListener
    {
        private JFrame parent;
        
        public ConnectionOptionsListener(JFrame parent)
        {
            this.parent = parent;
        }
        
        public void actionPerformed(ActionEvent e)
        {
            new ConnectionManagerWindow(parent).setVisible(true);
        }
    }
    
    class SendActionListener implements ActionListener
    {
        long portHandle;

        private void setHandle(long portHandle)
        {
            this.portHandle = portHandle;
        }

        /*private int charToInt(char c)
        {
            if(c >= '0' && c <= '9')
            {
                return (int)(c-48);
            }
            if(c >= 'A' && c <= 'F')
            {
                return (int)(c-55);
            }
            if(c >= 'a' && c <= 'f')
            {
                return (int)(c-(55+32));
            }
            return -1;
        }*/

        @Override
        public void actionPerformed(ActionEvent e)
        {
            //interpret the string as a byte[] and send it.
            /*String data = fooToSend.getText();
            byte[] sendStuff = new byte[data.length()/2];
            int dummy = 0;
            int state = 0;

            for(int i = 0;i < data.length();i++)
            {
                switch(state)
                {
                    //put char into dummy
                    case 0:
                    {
                        dummy = charToInt(data.charAt(i));
                        if(charToInt(data.charAt(i)) == -1)
                        {
                            return;
                        }
                        state = 1;
                        break;
                    }

                    case 1:
                    {
                        if(charToInt(data.charAt(i)) == -1)
                        {
                            return;
                        }
                        dummy |= charToInt(data.charAt(i));
                        state = 0;
                        break;
                    }
                }
               }*/

            SerialPortInterface foozor = new SerialPortInterface();

            byte[] commandBytes = new byte[] {0x01, 0x00, 0x00, 0x00, 0x00};
            commandBytes[3] = (byte)(fooToSend.getText().length() >> 8);
            commandBytes[4] = (byte)(fooToSend.getText().length() & 0xff);

            foozor.writeToPort(portHandle, commandBytes);
            foozor.writeToPort(portHandle, fooToSend.getText().getBytes());
        }
    }
    
    //"Spits" out all the serial data we receive.
    class SerialSpitter extends SerialObserver
    {
        void bytesReceived(byte[] data)
        {
            for(byte b:data)
            {
                System.out.print((char)b);
            }
            System.out.println();
        }
        
    }
    //</editor-fold>
    
    //variables
    //<editor-fold>
    //menu variables
    JMenuBar mainMenuBar;
    
    JMenu connectMenu;
    JMenuItem connectionOptions;
    JMenuItem pingOption;
    
    JMenu programMenu;
    JMenuItem programRangeOption;
    JMenuItem programFileOption;
    
    JMenu readMenu;
    JMenuItem readRangeOption;
    JMenuItem fullMemoryDumpOption;
    
    //text fields
    JTextArea fooToSend;
    JButton fooSender;
    
    //</editor-fold>
    
    
    public MainWindow()
    {
        //set up the serial port
        //<editor-fold>
        SerialPortInterface foo = new SerialPortInterface();
        String portToUse = foo.firstPortAvailable();
        long portHandle = foo.setUpStandard(portToUse);
        System.out.println("brate set as " + foo.setBaudRate(portHandle, 115200));
        //</editor-fold>
        
        //set up menu bar
        //<editor-fold>
        //first, set up submenus
        connectionOptions = new JMenuItem("options");
        connectionOptions.addActionListener(new ConnectionOptionsListener(this));
        
        pingOption = new JMenuItem("ping programmer");
        pingOption.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        programRangeOption = new JMenuItem("program range");
        programRangeOption.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        programFileOption = new JMenuItem("program file");
        programFileOption.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        readRangeOption = new JMenuItem("readRange");
        readRangeOption.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        fullMemoryDumpOption = new JMenuItem("full memory dump");
        fullMemoryDumpOption.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        //next, set up menu items and add submenus
        connectMenu = new JMenu("connect");
        connectMenu.add(connectionOptions);
        connectMenu.add(pingOption);
        
        programMenu = new JMenu("program");
        programMenu.add(programRangeOption);
        programMenu.add(programFileOption);
        
        readMenu = new JMenu("read");
        readMenu.add(readRangeOption);
        readMenu.add(fullMemoryDumpOption);
        
        //now, set up the main menu bar
        mainMenuBar = new JMenuBar();
        mainMenuBar.add(connectMenu);
        mainMenuBar.add(programMenu);
        mainMenuBar.add(readMenu);
        
        //set this menu bar as the frame's menu
        this.setJMenuBar(mainMenuBar);
        //</editor-fold>
        
        //set up the text areas and buttons here...
        //<editor-fold>
        fooToSend = new JTextArea();
        fooToSend.setFont(new Font(Font.MONOSPACED, 0, 12));
        
        fooSender = new JButton("send (ascii) stuff");
        SendActionListener sender = new SendActionListener();
        sender.setHandle(portHandle);
        fooSender.addActionListener(sender);
        //</editor-fold>
        
        //lay everything out
        //<editor-fold>
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        this.setLayout(layout);
        
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        this.add(fooSender, c);
        
        c.gridy = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        this.add(fooToSend, c);
        
        //make size reasonable
        this.setSize(500, 430);
        //</editor-fold>
        
        //make an observer and set up the loop
        //<editor-fold>
        SerialSpitter sp = new SerialSpitter();

        SerialListeningLoop sll = new SerialListeningLoop(portHandle, foo);
        sll.observers.add(sp);
        new Thread(sll).start();
        //</editor-fold>
    }
}
