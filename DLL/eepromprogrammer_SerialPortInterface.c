#include "eepromprogrammer_SerialPortInterface.h"
#include <string.h>
#include <stdlib.h>
#include <Windows.h>
#include <conio.h>

JNIEXPORT jlong JNICALL Java_eepromprogrammer_SerialPortInterface_openPort(JNIEnv* env, jobject obj, jstring string)
{
	HANDLE portHandle = CreateFile((*env)->GetStringUTFChars(env, string, 0),  
			GENERIC_READ | GENERIC_WRITE, 
			0, 
			0, 
			OPEN_EXISTING,
			FILE_ATTRIBUTE_NORMAL,
			0);

	return (jlong)portHandle;
}

JNIEXPORT jboolean JNICALL Java_eepromprogrammer_SerialPortInterface_closePort(JNIEnv* env, jobject obj, jlong portHandle)
{
	if(CloseHandle((HANDLE)portHandle) == 0)
		return JNI_FALSE;
	else
		return JNI_TRUE;
}

JNIEXPORT jstring JNICALL Java_eepromprogrammer_SerialPortInterface_firstPortAvailable(JNIEnv* env, jobject obj)
{
	HANDLE portHandle;
	//remember, not C99.
	CHAR portNum = 0;

	char javaPortName[16];
	LPSTR portName = (LPSTR)malloc(6*sizeof(CHAR));
	LPCSTR portNameConst = (LPCSTR)portName;
	strcpy(portName, "COM0");
	portName[4] = '\0';



	for(portNum = 0;portNum < 10;portNum++)
	{
		*(portName+3) = portNum+48;
		portHandle = CreateFile(portNameConst,
					GENERIC_READ | GENERIC_WRITE,
					0,
					0,
					OPEN_EXISTING,
					FILE_ATTRIBUTE_NORMAL,
					0);
		if(portHandle != INVALID_HANDLE_VALUE)
		{
			int copyI;
			for(copyI = 0;*(portName+copyI-1) != (CHAR)'\0';copyI++)
			{
				javaPortName[copyI] = (char)(*(portName+copyI));
			}
			
			//close the port, or we won't be able to use it later.
			CloseHandle(portHandle);
			
			//no memory leaks
			free(portName);

			return (*env)->NewStringUTF(env, javaPortName);
		}
	}
	javaPortName[0] = '\0';
	return (*env)->NewStringUTF(env, javaPortName);
}

JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_writeToPort(JNIEnv* env, jobject obj, jlong portHandle, jbyteArray data)
{
	DWORD numBytesWritten;
	
	jsize dataLength;
	jbyte* nativeData = 0;
	int numBytesToWrite = 1;
	int writeSuccessful;
	
	if((HANDLE)portHandle == INVALID_HANDLE_VALUE)
	{
		return (jint)1;
	}
	
	//successfully opened a port.  Write data to it.
	dataLength = (*env)->GetArrayLength(env, data);
	
	//if the length of the data array is 0, we can't do nuttin.
	if(dataLength == 0)
	{
		return (jint)2;
	}

	nativeData = (*env)->GetByteArrayElements(env, data, 0);
	
	//make an int to hold bytes actually written, and actually write data.
	//if the WriteFile function returns 0, data write was unsuccessful.
	writeSuccessful = WriteFile((HANDLE)portHandle, nativeData, dataLength, &numBytesWritten, NULL);
	//(*env)->ReleaseByteArrayElements(env, data, nativeData, 0);
	//CloseHandle(portHandle);
	
	if(writeSuccessful == 0)
		return (jint)3;
	
	//if less bytes than specified were written, we in trubble.
	if(numBytesWritten < numBytesToWrite)
		return (jint)4;
	
	//no errors.  Ret true.
	return (jint)0;
}

JNIEXPORT jbyteArray JNICALL Java_eepromprogrammer_SerialPortInterface_readPort(JNIEnv* env, jobject obj, jlong portHandle, jint length)
{
	//we first read up to length bytes from the serial port, then, make a new byte array with that data copied into it and return it.

	// instance an object of COMMTIMEOUTS.
	COMMTIMEOUTS comTimeOut;  
	
	//record how many were read.
	DWORD numBytesRead;
	jbyteArray retArr;
	jbyte* retArrCopy;
	
	int readSuccessful;
	char* container = (char*)malloc((int)length*sizeof(char));
	
	if((HANDLE)portHandle == INVALID_HANDLE_VALUE)
	{
		return (jbyteArray)0;
	}
	
	//All fields of the COMMTIMEOUTS structure are in milliseconds.
	//Time allowed between characters.
	comTimeOut.ReadIntervalTimeout = 2;
	
	//The following 2 fields define the total time-out of the read
	//operation.  The time-out is given by
	//t = ReadTotalTimeoutMultiplier*bytesToRead + ReadTotalTimeoutConstant.
	comTimeOut.ReadTotalTimeoutMultiplier = 1;
	comTimeOut.ReadTotalTimeoutConstant = 1;
	
	//Same as previous 2 fields but for write, not read.
	comTimeOut.WriteTotalTimeoutMultiplier = 3;
	comTimeOut.WriteTotalTimeoutConstant = 2;
	
	// set the time-out parameter into device control.
	SetCommTimeouts((HANDLE)portHandle,&comTimeOut);
	
	readSuccessful = ReadFile((HANDLE)portHandle, container, length, &numBytesRead, NULL);

	//if read was not successful, return null.
	if(readSuccessful == 0)
	{
		free(container);
		return (jbyteArray)0;
	}
	
	//create a new byte array and copy the contents of the container over to it.
	retArr = (*env)->NewByteArray(env, (jsize)numBytesRead);
	retArrCopy = (*env)->GetByteArrayElements(env, retArr, 0);
	memcpy(retArrCopy, container, (int)numBytesRead);
	(*env)->ReleaseByteArrayElements(env, retArr, retArrCopy, 0);
	
	free(container);
	return retArr;
}

//error codes
//0 - no error
//1 - invalid brate
//2 - unable to open port
//3 - error closing file
//4 - error manipulating port settings
//5 - invalid option
//6 - other error
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_configureSerialPort(JNIEnv* env, jclass obj, jlong portHandle, jint option, jint setting)
{
	DCB portOptions;
	DWORD validBrates[] = {CBR_110, CBR_300, CBR_600, CBR_1200, CBR_2400, CBR_4800, CBR_9600, CBR_14400, CBR_19200, CBR_38400, CBR_57600, CBR_115200, CBR_128000, CBR_256000};
	DWORD setBrate = 0;
	
	portOptions.DCBlength = sizeof(DCB);
	
	if((HANDLE)portHandle == INVALID_HANDLE_VALUE)
		return (jint)2;
	
	if(GetCommState((HANDLE)portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	switch((int)option)
	{
		//BaudRate
		case 0:
		{
			const int numOfBrates = 14;
			
			//make sure the brate is "legal"
			int i;
			for(i = 0;i < numOfBrates;i++)
			{
				if((int)setting == (int)validBrates[i])
				{
					setBrate = validBrates[i];
					break;
				}
			}

			if(setBrate == 0)
			{
				return (jint)1;
			}
			
			portOptions.BaudRate = setBrate;
			break;
		}
		
		//fParity
		case 1:
		{
			if((int)setting)
				portOptions.fParity = TRUE;
			else
				portOptions.fParity = FALSE;
			
			break;
		}
		
		//fOutxCtsFlow
		case 2:
		{
			if((int)setting)
				portOptions.fOutxCtsFlow = TRUE;
			else
				portOptions.fOutxCtsFlow = FALSE;
			
			break;
		}
		
		//fOutxDsrFlow
		case 3:
		{
			if((int)setting)
				portOptions.fOutxDsrFlow = TRUE;
			else
				portOptions.fOutxDsrFlow = FALSE;
			
			break;
		}
		
		//fDtrControl
		case 4:
		{
			portOptions.fDtrControl = (int)setting;
			
			break;
		}
		
		//fDsrSensitivity
		case 5:
		{
			if((int)setting)
				portOptions.fDsrSensitivity = TRUE;
			else
				portOptions.fDsrSensitivity = FALSE;
			
			break;
		}
		
		//fTXContinueOnXOff
		case 6:
		{
			if((int)setting)
				portOptions.fTXContinueOnXoff = TRUE;
			else
				portOptions.fTXContinueOnXoff = FALSE;
			
			break;
		}
		
		//fOutX
		case 7:
		{
			if((int)setting)
				portOptions.fOutX = TRUE;
			else
				portOptions.fOutX = FALSE;
			
			break;
		}
		
		//fInX
		case 8:
		{
			if((int)setting)
				portOptions.fInX = TRUE;
			else
				portOptions.fInX = FALSE;
			
			break;
		}
		
		//fErrorChar
		case 9:
		{
			if((int)setting)
				portOptions.fErrorChar = TRUE;
			else
				portOptions.fErrorChar = FALSE;
			
			break;
		}
		
		//fNull
		case 10:
		{
			if((int)setting)
				portOptions.fNull = TRUE;
			else
				portOptions.fNull = FALSE;
			
			break;
		}
		
		//fRtsControl
		case 11:
		{
			portOptions.fRtsControl = (int)setting;
			
			break;
		}
		
		//fAbortOnError
		case 12:
		{
			if((int)setting)
				portOptions.fAbortOnError = TRUE;
			else
				portOptions.fAbortOnError = FALSE;
			
			break;
		}
		
		//ByteSize
		case 13:
		{
			portOptions.ByteSize = (int)setting;
			
			break;
		}
		
		//Parity
		case 14:
		{
			portOptions.Parity = (int)setting;
			
			break;
		}
		
		//StopBits
		case 15:
		{
			portOptions.StopBits = (int)setting;
			
			break;
		}
		
		default:
		{
			return 5;
		}
	}
	
	if(SetCommState((HANDLE)portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	return (jint)0;
}

//0 is false, nonzero is true.
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_handleValid(JNIEnv* env, jclass obj, jlong portHandle)
{
	DCB foo;
	foo.DCBlength = sizeof(DCB);
	
	return GetCommState((HANDLE)portHandle, &foo);
}

void main(){}
