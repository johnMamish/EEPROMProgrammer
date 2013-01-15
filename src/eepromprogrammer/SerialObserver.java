/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eepromprogrammer;

/**
 *
 * @author John
 */
public abstract class SerialObserver
{
    /*
     * This method is called whenever a new array of bytes is recieved.
     */
    abstract void bytesReceived(byte[] data);
    
    /*
     * Whenever the SerialListeningLoop checks the status of the connection, 
     * this method is called.
     */
    abstract void connectionStatusUpdated(boolean connected);
}
