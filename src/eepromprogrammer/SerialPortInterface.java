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
    
    public native boolean portNameValid(String portName);
    public native String firstPortAvailable();
    public native boolean writeToPort(String portName, byte[] data);
    public native int setBaudRate(String portName, int brate);
    public native int setStopBits(String portName, int stopBits);
    public native int setByteSize(String portName, int byteSize);
    
    private final int RTS_CONTROL_DISABLE = 0x00;
    private final int RTS_CONTROL_ENABLE = 0x01;
    private final int RTS_CONTROL_HANDSHAKE = 0x02;
    private final int RTS_CONTROL_TOGGLE = 0x03;

    private native int nativeSetParity(String portName, int parityType);
    private native int setParityOn(String portName, boolean parityOn);
    private native int setOutXCTSDSR(String portName, boolean enabledForXonXoff);
    private native int setDTRControl(String portName, int DTRMode);
    private native int setDiscardNull(String portName, boolean discardNull);
    
    public SerialPortInterface()
    {
        System.load("C:\\Users\\John\\Documents\\NetBeansProjects\\EEPROMProgrammer\\DLL\\eepromprogrammer_SerialPortInterface.dll");
    }
    
    public void setUpStandard(String portName)
    {
        //setDiscardNull(portName, false);
        setByteSize(portName, 8);
        this.setParity(portName, NO_PARITY);
        this.setOutXCTSDSR(portName, true);
        this.setDTRControl(portName, this.RTS_CONTROL_HANDSHAKE);
        //this.setDiscardNull(portName, false);
    }
    
    public int setParity(String portName, int parityType)
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
