/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eepromprogrammer;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
            new ConnectionManagerWindow(parent, sll).setVisible(true);
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
            if((this.portHandle = sll.portHandle) == SerialPortInterface.INVALID_PORT_HANDLE)
            {
                JOptionPane.showMessageDialog(null, "Warning: No connection is available.  Action cannot be completed.", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else
            {
                byte[] sendBytes = new byte[fooToSend.getText().length()+5];
                sendBytes[0] = 0x01;
                sendBytes[1] = 0x00;
                sendBytes[2] = 0x00;
                sendBytes[3] = (byte)(fooToSend.getText().length() >> 8);
                sendBytes[4] = (byte)(fooToSend.getText().length() & 0xff);
                for(int i = 0;i < fooToSend.getText().getBytes().length;i++)
                {
                    sendBytes[i+5] = fooToSend.getText().getBytes()[i];
                }

                SerialPortInterface.writeToPort(portHandle, sendBytes);
            }
        }
    }
    
    class SendFileListener implements ActionListener
    {
        private JFrame parent;
        
        SendFileListener(JFrame parent)
        {
            this.parent = parent;
        }
        public void actionPerformed(ActionEvent e)
        {
            //use a FileDialog to get the file we want to write.
            FileDialog fd = new FileDialog(parent, "select a file", FileDialog.LOAD);
            fd.setMultipleMode(false);
            fd.setFilenameFilter(new FilenameFilter()
            {
                //accept all files.
                public boolean accept(File dir, String name)
                {
                    return true;
                }
            });
            fd.setVisible(true);
            String filepath = fd.getDirectory()+fd.getFile();
            File writeFile = new File(filepath);
            
            //check to make sure the file exists.  If it doesn't, alert the user
            //and return.
            if(!writeFile.exists())
            {
                JOptionPane.showMessageDialog(null, "Warning: That file doesn't exist.", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            //if the file is null for some reason, 
            //Get the contents of the file and ask the user if they want to
            //write the file before we send it to the EEPROM.
            int fileLength = (int)writeFile.length();
            int confirm = JOptionPane.showOptionDialog(parent,
                    "Writing 0x" + Integer.toHexString(fileLength) + " bytes to the EEPROM\n"
                    +"starting at address 0x" + Integer.toHexString(0) + ".",
                    "Confirm write",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, null, null);
            //return if the user canceled.
            if(confirm == JOptionPane.NO_OPTION)
            {
                return;
            }
            
            //user agreed.  Time to write stuff!
            //first, check to make sure we have a valid handle.
            if((sll.portHandle) == SerialPortInterface.INVALID_PORT_HANDLE)
            {
                JOptionPane.showMessageDialog(null, "Warning: No connection is available.  Action cannot be completed.", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else
            {
                //set up the array to send to the programmer.
                byte[] sendBytes = new byte[(int)writeFile.length()+5];
                sendBytes[0] = 0x01;
                sendBytes[1] = 0x00;
                sendBytes[2] = 0x00;
                sendBytes[3] = (byte)(fileLength >> 8);
                sendBytes[4] = (byte)(fileLength & 0xff);
                
                //set up the file stream, then read the bytes in the file into
                //the array we are going to send to the EEPROM.
                FileInputStream in = null;
                try
                {
                    in = new FileInputStream(writeFile);
                    int byteRead;
                    
                    //keep in mind, EOF is a -1, but because we are using an int
                    //(which is 32-bits wide), EOF is 0xffffffff, not 0xff.  Because
                    //of this, we can still coherently read 0xff bytes from the file.
                    for(int i = 0;((byteRead = in.read()) != -1) && (i < fileLength);i++)
                    {
                        sendBytes[i+5] = (byte)(byteRead & 0xff);
                    }
                }
                catch(IOException fileFailure)
                {
                    //the file read failed.
                    JOptionPane.showMessageDialog(null, "Warning: Read from file failed, EEPROM not written.", "Warning", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SerialPortInterface.writeToPort(sll.portHandle, sendBytes);
            }
        }
    }
    
    //"Spits" out all the serial data we receive.
    class SerialSpitter extends SerialObserver
    {
        private boolean connected = false;
        
        void bytesReceived(byte[] data)
        {
            for(byte b:data)
            {
                System.out.print((char)b);
            }
            System.out.println();
        }
        
        void connectionStatusUpdated(boolean connected)
        {
            if(connected != this.connected)
            {
                if(connected)
                {
                    connectionStatus.setText(MainWindow.CONNECTED_MESSAGE);
                }
                else
                {
                    connectionStatus.setText(MainWindow.DISCONNECTED_MESSAGE);
                }
            }
            this.connected = connected;
        }
    }
    //</editor-fold>
    
    //variables
    //<editor-fold>
    //Serial Listening Loop
    SerialListeningLoop sll;
    
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
    JButton clearEEPROM;
    JLabel connectionStatus;
    
    //messages
    public static final String CONNECTED_MESSAGE = "connected";
    public static final String DISCONNECTED_MESSAGE = "disconnected";
    
    //</editor-fold>
    
    public MainWindow()
    {
        //set up menu bar
        //<editor-fold>
        //first, set up submenus
        connectionOptions = new JMenuItem("options");
        connectionOptions.addActionListener(new ConnectionOptionsListener(this));
        
        pingOption = new JMenuItem("ping programmer");
        pingOption.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(sll.portHandle == SerialPortInterface.INVALID_PORT_HANDLE)
                {
                    JOptionPane.showMessageDialog(null, "Warning: No connection is available.  Action cannot be completed.", "Warning", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    SerialPortInterface.writeToPort(sll.portHandle, new byte[]{0x03});
                    
                    long startWaitTime = System.currentTimeMillis();
                    byte[] pingResult = {0};
                    pingResult[0] = 0;
                    
                    //read port until we have recieved some kind of response or we have timed out.
                    while(((pingResult.length == 0) || (pingResult[0] == 0x00)) && ((System.currentTimeMillis()-startWaitTime) < 250))
                    {
                        pingResult = SerialPortInterface.readPort(sll.portHandle, 1);
                        if((pingResult == null))
                            break;
                    }
                    if((pingResult == null) || (pingResult.length == 0))
                    {
                        JOptionPane.showMessageDialog(null, "Warning: No connection is available.  Action cannot be completed.", "Warning", JOptionPane.ERROR_MESSAGE);
                    }
                    else if((System.currentTimeMillis()-startWaitTime) >= 250)
                    {
                        JOptionPane.showMessageDialog(null, "Warning: Ping returned incorrect result." + (int)pingResult[0], "Warning", JOptionPane.ERROR_MESSAGE);
                    }
                    else if(pingResult[0] != (byte)(-86))
                    {
                        JOptionPane.showMessageDialog(null, "Warning: Ping returned incorrect result." + (int)pingResult[0], "Warning", JOptionPane.ERROR_MESSAGE);
                        SerialPortInterface.closePort(sll.portHandle);
                        return;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Ping successful.", "Ping Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        
        programRangeOption = new JMenuItem("program range");
        programRangeOption.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        programFileOption = new JMenuItem("program file");
        programFileOption.addActionListener(new SendFileListener(this));
        
        readRangeOption = new JMenuItem("readRange");
        readRangeOption.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(sll.portHandle == SerialPortInterface.INVALID_PORT_HANDLE)
                {
                    JOptionPane.showMessageDialog(null, "Warning: No connection is available.  Action cannot be completed.", "Warning", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    SerialPortInterface.writeToPort(sll.portHandle, new byte[] {0x02, 0x00, 0x00, 0x02, 0x00});
                }
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
        fooSender.addActionListener(sender);
        
        clearEEPROM = new JButton("Clear EEPROM");
        clearEEPROM.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(sll.portHandle == SerialPortInterface.INVALID_PORT_HANDLE)
                {
                    JOptionPane.showMessageDialog(null, "Warning: No connection is available.  Action cannot be completed.", "Warning", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                else
                {
                    byte[] sendBytes = new byte[0x2000+5];
                    sendBytes[0] = 0x01;
                    sendBytes[1] = 0x00;
                    sendBytes[2] = 0x00;
                    sendBytes[3] = 0x20;
                    sendBytes[4] = 0x00;

                    for(int i = 0;i < 0x2000;i++)
                    {
                        sendBytes[i+5] = (byte)0xff;
                    }

                    SerialPortInterface.writeToPort(sll.portHandle, sendBytes);
                }
            }
        });
        
        connectionStatus = new JLabel(this.DISCONNECTED_MESSAGE);
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
        
        c.gridx = 1;
        c.weightx = 1;
        this.add(clearEEPROM, c);
        
        c.gridx = 2;
        this.add(connectionStatus, c);
        
        c.gridx = 0;
        c.gridwidth = 3;
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

        sll = new SerialListeningLoop();
        sll.observers.add(sp);
        new Thread(sll).start();
        //</editor-fold>
    }
}
