/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eepromprogrammer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
    JTextField memDump;
    //</editor-fold>
    
    
    public MainWindow()
    {
        //debug fun to test native serial port method(s)
        SerialPortInterface foo = new SerialPortInterface();
        long fooTime = System.currentTimeMillis();
        String portToUse = foo.firstPortAvailable();
        System.out.println(portToUse + " is the port we're using.");
        foo.setUpStandard(portToUse);
        
        System.out.println("time taken to find port: " + Long.toString(System.currentTimeMillis()-fooTime) + "ms");
        
        System.out.println("setting baud rate gave the result " + foo.setBaudRate(portToUse, 115200));
        
        System.out.println("result = " + foo.writeToPort(portToUse, " This is awesome!".getBytes()));
        
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
        
        //set up the text areas here...
        //<editor-fold>
        //TODO set up text areas
        //</editor-fold>
        
        //lay everything out
        //<editor-fold>
        BorderLayout layout = new BorderLayout();
        //layout.addLayoutComponent(mainMenuBar, BorderLayout.NORTH);
        this.setLayout(layout);
        
        //make size reasonable
        this.setSize(500, 430);
        //</editor-fold>
    }
}
