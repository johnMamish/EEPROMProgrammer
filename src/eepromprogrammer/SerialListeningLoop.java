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
    private long portHandle;
    private SerialPortInterface spInterface;
    
    public SerialListeningLoop(long portHandle, SerialPortInterface spif)
    {
        blockSize = this.DEFAULT_BLOCK_SIZE;
        observers = new ArrayList<SerialObserver>();
        this.spInterface = spif;
        this.portHandle = portHandle;
        running = false;
    }
    
    public SerialListeningLoop(long portHandle, SerialPortInterface spif, int blockSize)
    {
        this.blockSize = blockSize;
        observers = new ArrayList<SerialObserver>();
        this.spInterface = spif;
        this.portHandle = portHandle;
        running = false;
    }
    
    //poll serial port for new input every 50ms.
    public void run()
    {
        running = true;
        while(true)
        {
            while(true)
            {
                try{Thread.sleep(SLEEP_LENGTH);}
                catch(InterruptedException ie){}
                System.out.println("handle number = " + portHandle);
                
                byte[] receivedData = spInterface.readPort(portHandle, blockSize);
                
                //inform all the observers if we actually recieved something.
                if((receivedData != null) && (receivedData.length > 0))
                {
                    for(SerialObserver so:observers)
                    {
                        so.bytesReceived(receivedData);
                    }
                }
            }
        }
    }
}
