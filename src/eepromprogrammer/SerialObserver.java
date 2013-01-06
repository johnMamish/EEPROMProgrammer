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
    abstract void bytesReceived(byte[] data);
}
