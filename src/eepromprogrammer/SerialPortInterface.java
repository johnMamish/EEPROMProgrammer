/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eepromprogrammer;

/**
 *
 * @author John
 */
public abstract class SerialPortInterface
{  
    public final static long INVALID_PORT_HANDLE = -1;
    
    public final static int EVEN_PARITY = 0x02;
    public final static int MARK_PARITY = 0x03;
    public final static int NO_PARITY = 0x00;
    public final static int ODD_PARITY = 0x01;
    public final static int SPACE_PARITY = 0x04;

    public final static int ONE_STOP_BIT = 0x00;
    public final static int ONE_POINT_FIVE_STOP_BITS = 0x01;
    public final static int TWO_STOP_BITS = 0x02;
    
    //these are passed into a non-native function to configure handshaking.
    //handshake configuration is a process more complex than the programmer
    //should have to deal with.
    public final static int HANDSHAKE_NONE = 0x00;
    public final static int HANDSHAKE_XON_XOFF = 0x01;
    public final static int HANDSHAKE_RTS_CTS = 0x02;
    
    public native static long openPort(String portName);
    public native static boolean closePort(long portHandle);
    public native static String firstPortAvailable();
    public native static int writeToPort(long portHandle, byte[] data);
    public native static byte[] readPort(long portHandle, int length);
    public native static int handleValid(long portHandle);
    
    private final static int RTS_CONTROL_DISABLE = 0x00;
    private final static int RTS_CONTROL_ENABLE = 0x01;
    private final static int RTS_CONTROL_HANDSHAKE = 0x02;
    private final static int RTS_CONTROL_TOGGLE = 0x03;
    
    private final static int BAUDRATE = 0;
    private final static int FPARITY = 1;
    private final static int FOUTXCTSFLOW = 2;
    private final static int FOUTXDSRFLOW = 3;
    private final static int FDTRCONTROL = 4;
    private final static int FDSRSENSITIVITY = 5;
    private final static int FTXCONTINUEONXOFF = 6;
    private final static int FOUTX = 7;
    private final static int FINX = 8;
    private final static int FERRORCHAR = 9;
    private final static int FNULL = 10;
    private final static int FRTSCONTROL = 11;
    private final static int FABORTONERROR = 12;
    private final static int BYTESIZE = 13;
    private final static int PARITY = 14;
    private final static int STOPBITS = 15;
    
    private native static int configureSerialPort(long portHandle, int option, int setting);
    
    public static void LoadLibrary()
    {
        System.load("C:\\Users\\John\\Documents\\NetBeansProjects\\EEPROMProgrammer\\DLL\\eepromprogrammer_SerialPortInterface.dll");
    }
    
    public static long setUpStandard(String portName)
    {
        long portHandle = openPort(portName);
        setBaudRate(portHandle, 115200);
        setByteSize(portHandle, 8);
        setParity(portHandle, NO_PARITY);
        System.out.println("handshake setup = " + setHandshaking(portHandle, HANDSHAKE_RTS_CTS));
        return portHandle;
    }
    
    public static int setParity(long portHandle, int parityType)
    {
        if(parityType == NO_PARITY)
        {
            configureSerialPort(portHandle, FPARITY, 0);
        }
        else
        {
            configureSerialPort(portHandle, FPARITY, 1);
        }
        
        return configureSerialPort(portHandle, PARITY, parityType);
    }
    
    public static int setBaudRate(long portHandle, int baudRate)
    {
        return configureSerialPort(portHandle, BAUDRATE, baudRate);
    }
    
    public static int setStopBits(long portHandle, int stopBits)
    {
        return configureSerialPort(portHandle, STOPBITS, stopBits);
    }
    
    public static int setByteSize(long portHandle, int bitsPerByte)
    {
        return configureSerialPort(portHandle, BYTESIZE, bitsPerByte);
    }
    
    public static int setDiscardNull(long portHandle, boolean discardNull)
    {
        int temp = 0;
        if(discardNull)
        {
            temp = 1;
        }
        
        return configureSerialPort(portHandle, FNULL, temp);
    }
    
    public static int setHandshaking(long portHandle, int handshakeType)
    {
        int[] options;
        int[] thingsToConfigure = {FOUTXCTSFLOW, FOUTXDSRFLOW, FOUTX, FINX, FDTRCONTROL, FDSRSENSITIVITY, FTXCONTINUEONXOFF, FRTSCONTROL};
        
        switch(handshakeType)
        {
            case HANDSHAKE_NONE:
            {
                options = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
                break;
            }
                
            case HANDSHAKE_XON_XOFF:
            {
                options = new int[]{0, 0, 1, 1, 0, 0, 0, 0};
                break;
            }
            
            case HANDSHAKE_RTS_CTS:
            {
                options = new int[]{1, 1, 0, 0, 0, 0, 0, 0x02};
                break;
            }
            
            default:
            {
                return -1;
            }
        }
        
        int result = 0;
        for(int i = 0;i < options.length; i++)
        {
            result |= configureSerialPort(portHandle, thingsToConfigure[i], options[i]);
        }
        
        return result;
    }
}
