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
    public final int EVEN_PARITY = 0x02;
    public final int MARK_PARITY = 0x03;
    public final int NO_PARITY = 0x00;
    public final int ODD_PARITY = 0x01;
    public final int SPACE_PARITY = 0x04;

    public final int ONE_STOP_BIT = 0x00;
    public final int ONE_POINT_FIVE_STOP_BITS = 0x01;
    public final int TWO_STOP_BITS = 0x02;
    
    //these are passed into a non-native function to configure handshaking.
    //handshake configuration is a process more complex than the programmer
    //should have to deal with.
    public final int HANDSHAKE_NONE = 0x00;
    public final int HANDSHAKE_XON_XOFF = 0x01;
    public final int HANDSHAKE_RTS_CTS = 0x02;
    public final int HANDSHAKE_RTS_CTS_XON_XOFF = 0x03;
    
    public native long openPort(String portName);
    public native boolean closePort(long portHandle);
    public native String firstPortAvailable();
    public native int writeToPort(long portHandle, byte[] data);
    public native int setBaudRate(long portHandle, int brate);
    public native int setStopBits(long portHandle, int stopBits);
    public native int setByteSize(long portHandle, int byteSize);
    public native byte[] readPort(long portHandle, int length);
    
    private final int RTS_CONTROL_DISABLE = 0x00;
    private final int RTS_CONTROL_ENABLE = 0x01;
    private final int RTS_CONTROL_HANDSHAKE = 0x02;
    private final int RTS_CONTROL_TOGGLE = 0x03;

    private native int nativeSetParity(long portHandle, int parityType);
    private native int setParityOn(long portHandle, boolean parityOn);
    private native int setOutXCTSDSR(long portHandle, boolean enabledForXonXoff);
    private native int setDTRControl(long portHandle, int DTRMode);
    private native int setDiscardNull(long portHandle, boolean discardNull);
    
    public SerialPortInterface()
    {
        System.load("C:\\Users\\John\\Documents\\NetBeansProjects\\EEPROMProgrammer\\DLL\\eepromprogrammer_SerialPortInterface.dll");
    }
    
    public long setUpStandard(String portName)
    {
        long portHandle = openPort(portName);
        setDiscardNull(portHandle, false);
        setByteSize(portHandle, 8);
        this.setParity(portHandle, NO_PARITY);
        this.setOutXCTSDSR(portHandle, true);
        this.setDTRControl(portHandle, this.RTS_CONTROL_HANDSHAKE);
        return portHandle;
    }
    
    public int setParity(long portName, int parityType)
    {
        if(parityType == this.NO_PARITY)
        {
            this.setParityOn(portName, false);
        }
        else
        {
            this.setParityOn(portName, true);
        }
        
        return this.nativeSetParity(portName, parityType);
    }
    
    public int setHandshaking(String portName, int handshakeType)
    {
        return 0;
    }
}
