#include "eepromprogrammer_SerialPortInterface.h"
#include <string.h>
#include <stdlib.h>
#include <Windows.h>
#include <conio.h>

JNIEXPORT jboolean JNICALL Java_eepromprogrammer_SerialPortInterface_portNameValid(JNIEnv* env, jobject obj, jstring string)
{
	HANDLE portHandle = CreateFile((*env)->GetStringUTFChars(env, string, 0),  
			GENERIC_READ | GENERIC_WRITE, 
			0, 
			0, 
			OPEN_EXISTING,
			FILE_FLAG_OVERLAPPED,
			0);

	if(portHandle == INVALID_HANDLE_VALUE)
	{
		return JNI_FALSE;
	}
	else
	{
		return JNI_TRUE;
	}
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



	for(portNum = 0;portNum < 10;portNum++)
	{
		*(portName+3) = portNum+48;
		portHandle = CreateFile(portNameConst,
					GENERIC_READ | GENERIC_WRITE,
					0,
					0,
					OPEN_EXISTING,
					FILE_FLAG_OVERLAPPED,
					0);
		if(portHandle != INVALID_HANDLE_VALUE)
		{
			int copyI;
			for(copyI = 0;*(portName+copyI) != (CHAR)'\0';copyI++)
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

JNIEXPORT jboolean JNICALL Java_eepromprogrammer_SerialPortInterface_writeToPort(JNIEnv* env, jobject obj, jstring string, jbyteArray data)
{	
	//handle to the port
	HANDLE portHandle;
	
	//for the writefile function needs this.
	DWORD numBytesWritten;
	
	jsize dataLength;
	//jboolean copyMade = JNI_FALSE;
	jbyte* nativeData = 0;
	int numBytesToWrite = 1;
	int writeSuccessful;

	const jbyte* name = (*env)->GetStringUTFChars(env, string, 0);
	
	portHandle = CreateFile((LPCSTR)name,
				GENERIC_READ | GENERIC_WRITE,
				0,
				0,
				OPEN_EXISTING,
				FILE_ATTRIBUTE_NORMAL,
				0);
	//tried to open an invalid port, but failed.
	if(portHandle == INVALID_HANDLE_VALUE)
	{
		return JNI_FALSE;
	}
	
	//successfully opened a port.  Write data to it.
	dataLength = (*env)->GetArrayLength(env, data);
	
	//if the length of the data array is 0, we can't do nuttin.
	if(dataLength == 0)
	{
		return JNI_FALSE;
	}

	nativeData = (*env)->GetByteArrayElements(env, data, 0);
	
	//make an int to hold bytes actually written, and actually write data.
	//if the WriteFile function returns 0, data write was unsuccessful.
	writeSuccessful = WriteFile(portHandle, nativeData, dataLength, &numBytesWritten, NULL);
	//(*env)->ReleaseByteArrayElements(env, data, nativeData, 0);
	CloseHandle(portHandle);
	
	if(writeSuccessful == 0)
		return JNI_FALSE;
	
	//if less bytes than specified were written, we in trubble.
	if(numBytesWritten < numBytesToWrite)
		return JNI_FALSE;
	
	//no errors.  Ret true.
	return JNI_TRUE;
}

//error codes
//0 - no error
//1 - invalid brate
//2 - unable to open port
//3 - error closing file
//4 - error manipulating port settings
//4 - other error
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setBaudRate(JNIEnv* env, jobject obj, jstring string, jint brate)
{
	DCB portOptions;
	DWORD newBrate;
	DWORD validBrates[] = {CBR_110, CBR_300, CBR_600, CBR_1200, CBR_2400, CBR_4800, CBR_9600, CBR_14400, CBR_19200, CBR_38400, CBR_57600, CBR_115200, CBR_128000, CBR_256000};
	DWORD setBrate = 0;
	HANDLE portHandle;
	const int numOfBrates = 14;
	int nativeTargetBrate = (int)brate;
	const jbyte* name = (*env)->GetStringUTFChars(env, string, 0);
	
	//make sure the brate is "legal"
	int i;
	for(i = 0;i < numOfBrates;i++)
	{
		if(nativeTargetBrate == (int)validBrates[i])
		{
			setBrate = validBrates[i];
			break;
		}
	}

	if(setBrate == 0)
	{
		return (jint)1;
	}
	
	portHandle = CreateFile((LPCSTR)name,
				GENERIC_READ | GENERIC_WRITE,
				0,
				0,
				OPEN_EXISTING,
				FILE_ATTRIBUTE_NORMAL,
				0);
	
	if(portHandle == INVALID_HANDLE_VALUE)
	{
		return (jint)2;
	}
	
	//all preliminary stuff done, time to actually change the configuration.
	if(GetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	portOptions.BaudRate = setBrate;
	portOptions.DCBlength = sizeof(DCB);
	
	if(SetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	if(CloseHandle(portHandle) == 0)
	{
		return (jint)3;
	}
	
	return (jint)portOptions.BaudRate;
}

JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setStopBits(JNIEnv* env, jobject obj, jstring portName, jint stopBits)
{
	DCB portOptions;
	HANDLE portHandle;
	DWORD nativeStopBits = (DWORD)stopBits;
	const jbyte* name = (*env)->GetStringUTFChars(env, portName, 0);
	
	portHandle = CreateFile((LPCSTR)name,
				GENERIC_READ | GENERIC_WRITE,
				0,
				0,
				OPEN_EXISTING,
				FILE_ATTRIBUTE_NORMAL,
				0);
	
	if(portHandle == INVALID_HANDLE_VALUE)
	{
		return (jint)2;
	}
	
	//all preliminary stuff done, time to actually change the configuration.
	if(GetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	portOptions.StopBits = nativeStopBits;
	portOptions.DCBlength = sizeof(DCB);
	
	if(SetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	if(CloseHandle(portHandle) == 0)
	{
		return (jint)3;
	}
	
	return (jint)0;
}

JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setByteSize(JNIEnv* env, jobject obj, jstring portName, jint byteSize)
{
	DCB portOptions;
	HANDLE portHandle;
	DWORD nativeByteSize = (DWORD)byteSize;
	const jbyte* name = (*env)->GetStringUTFChars(env, portName, 0);
	
	portHandle = CreateFile((LPCSTR)name,
				GENERIC_READ | GENERIC_WRITE,
				0,
				0,
				OPEN_EXISTING,
				FILE_ATTRIBUTE_NORMAL,
				0);
	
	if(portHandle == INVALID_HANDLE_VALUE)
	{
		return (jint)2;
	}
	
	//all preliminary stuff done, time to actually change the configuration.
	if(GetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	portOptions.ByteSize = nativeByteSize;
	portOptions.DCBlength = sizeof(DCB);
	
	if(SetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	if(CloseHandle(portHandle) == 0)
	{
		return (jint)3;
	}
	
	return (jint)0;
}

JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_nativeSetParity(JNIEnv* env, jobject obj, jstring portName, jint parityOption)
{
	DCB portOptions;
	HANDLE portHandle;
	CHAR nativeParityOption = (CHAR)parityOption;
	const jbyte* name = (*env)->GetStringUTFChars(env, portName, 0);
	
	portHandle = CreateFile((LPCSTR)name,
				GENERIC_READ | GENERIC_WRITE,
				0,
				0,
				OPEN_EXISTING,
				FILE_ATTRIBUTE_NORMAL,
				0);
	
	if(portHandle == INVALID_HANDLE_VALUE)
	{
		return (jint)2;
	}
	
	//all preliminary stuff done, time to actually change the configuration.
	if(GetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	portOptions.Parity = nativeParityOption;
	portOptions.DCBlength = sizeof(DCB);
	
	if(SetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	if(CloseHandle(portHandle) == 0)
	{
		return (jint)3;
	}
	
	return (jint)0;
}

JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setParityOn(JNIEnv* env, jobject obj, jstring portName, jboolean parityOn)
{
	DCB portOptions;
	HANDLE portHandle;
	DWORD nativeParityOn;
	const jbyte* name = (*env)->GetStringUTFChars(env, portName, 0);
	
	if(parityOn == JNI_TRUE)
	{
		nativeParityOn = TRUE;
	}
	else
	{
		nativeParityOn = FALSE;
	}
	
	portHandle = CreateFile((LPCSTR)name,
				GENERIC_READ | GENERIC_WRITE,
				0,
				0,
				OPEN_EXISTING,
				FILE_ATTRIBUTE_NORMAL,
				0);
	
	if(portHandle == INVALID_HANDLE_VALUE)
	{
		return (jint)2;
	}
	
	//all preliminary stuff done, time to actually change the configuration.
	if(GetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	portOptions.fParity = nativeParityOn;
	portOptions.DCBlength = sizeof(DCB);
	
	if(SetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	if(CloseHandle(portHandle) == 0)
	{
		return (jint)3;
	}
	
	return (jint)0;
}

JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setOutXCTSDSR(JNIEnv* env, jobject obj, jstring portName, jboolean enable)
{
	DCB portOptions;
	HANDLE portHandle;
	DWORD nativeEnable;
	const jbyte* name = (*env)->GetStringUTFChars(env, portName, 0);
	
	if(enable == JNI_TRUE)
	{
		nativeEnable = TRUE;
	}
	else
	{
		nativeEnable = FALSE;
	}
	
	portHandle = CreateFile((LPCSTR)name,
				GENERIC_READ | GENERIC_WRITE,
				0,
				0,
				OPEN_EXISTING,
				FILE_ATTRIBUTE_NORMAL,
				0);
	
	if(portHandle == INVALID_HANDLE_VALUE)
	{
		return (jint)2;
	}
	
	//all preliminary stuff done, time to actually change the configuration.
	if(GetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	portOptions.fOutxCtsFlow = portOptions.fOutxDsrFlow = nativeEnable;
	portOptions.DCBlength = sizeof(DCB);
	
	if(SetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	if(CloseHandle(portHandle) == 0)
	{
		return (jint)3;
	}
	
	return (jint)0;
}

//
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setDTRControl(JNIEnv* env, jobject obj, jstring portName, jint dtrOption)
{
	DCB portOptions;
	HANDLE portHandle;
	DWORD nativeDTROption = (DWORD)dtrOption;
	const jbyte* name = (*env)->GetStringUTFChars(env, portName, 0);
	
	if(dtrOption == JNI_TRUE)
	{
		nativeDTROption = TRUE;
	}
	else
	{
		nativeDTROption = FALSE;
	}
	
	portHandle = CreateFile((LPCSTR)name,
				GENERIC_READ | GENERIC_WRITE,
				0,
				0,
				OPEN_EXISTING,
				FILE_ATTRIBUTE_NORMAL,
				0);
	
	if(portHandle == INVALID_HANDLE_VALUE)
	{
		return (jint)2;
	}
	
	//all preliminary stuff done, time to actually change the configuration.
	if(GetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	portOptions.fDtrControl = nativeDTROption;
	portOptions.DCBlength = sizeof(DCB);
	
	if(SetCommState(portHandle, &portOptions) == 0)
	{
		return (jint)4;
	}
	
	if(CloseHandle(portHandle) == 0)
	{
		return (jint)3;
	}
	
	return (jint)0;
}


void main(){}
