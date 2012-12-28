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

/*void main(){}*/
