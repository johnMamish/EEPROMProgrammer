/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eepromprogrammer;

/**
 *
 * @author John
 */
public class SerialPortInterface
{
    public native boolean portNameValid(String portName);
    public native String firstPortAvailable();
    
    public SerialPortInterface()
    {
        System.load("C:\\Users\\John\\Documents\\NetBeansProjects\\EEPROMProgrammer\\DLL\\eepromprogrammer_SerialPortInterface.dll");
    }
}
