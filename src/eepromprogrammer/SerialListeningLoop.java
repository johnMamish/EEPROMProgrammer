/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eepromprogrammer;

import java.util.ArrayList;

/**
 *
 * @author John
 */
public class SerialListeningLoop implements Runnable
{
    private int blockSize;
    public ArrayList<SerialObserver> observers;
    private final int DEFAULT_BLOCK_SIZE = 256;
    
    //time to sleep in between reads in milliseconds
    private final int SLEEP_LENGTH = 40;
    private boolean running;
    public long portHandle;
    
    public SerialListeningLoop()
    {
        blockSize = this.DEFAULT_BLOCK_SIZE;
        observers = new ArrayList<SerialObserver>();
        this.portHandle = SerialPortInterface.INVALID_PORT_HANDLE;
        running = false;
    }
    
    public SerialListeningLoop(int blockSize)
    {
        this.blockSize = blockSize;
        observers = new ArrayList<SerialObserver>();
        this.portHandle = SerialPortInterface.INVALID_PORT_HANDLE;
        running = false;
    }
    
    public void start()
    {
        running = true;
    }
    
    public void stop()
    {
        running = false;
    }
    
    public void close()
    {
        this.stop();
        SerialPortInterface.closePort(this.portHandle);
    }
    
    public void open(long handle)
    {
        this.portHandle = handle;
        this.start();
    }
    
    //poll serial port for new input every 50ms.
    public void run()
    {
        while(true)
        {
            try{Thread.sleep(SLEEP_LENGTH);}
            catch(InterruptedException ie){}
            
            while(running)
            {
                byte[] receivedData = SerialPortInterface.readPort(portHandle, blockSize);
                //System.out.println("portHandle validation = " + SerialPortInterface.handleValid(portHandle));
                
                //inform all the observers if we actually recieved something.
                if((receivedData != null) && (receivedData.length > 0))
                {
                    for(SerialObserver so:observers)
                    {
                        so.bytesReceived(receivedData);
                    }
                }
                
                for(SerialObserver so:observers)
                {
                    so.connectionStatusUpdated(SerialPortInterface.handleValid(portHandle) != 0);
                }
            }
        }
    }
}