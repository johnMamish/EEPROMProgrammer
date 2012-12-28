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
			
			//no memory leaks
			free(portName);

			return (*env)->NewStringUTF(env, javaPortName);
		}
	}
	javaPortName[0] = '\0';
	return (*env)->NewStringUTF(env, javaPortName);
}

void main(){}
