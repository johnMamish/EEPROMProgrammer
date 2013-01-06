/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class eepromprogrammer_SerialPortInterface */

#ifndef _Included_eepromprogrammer_SerialPortInterface
#define _Included_eepromprogrammer_SerialPortInterface
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    openPort
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_eepromprogrammer_SerialPortInterface_openPort
  (JNIEnv *, jobject, jstring);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    closePort
 * Signature: (J)J
 */
JNIEXPORT jboolean JNICALL Java_eepromprogrammer_SerialPortInterface_closePort
  (JNIEnv *, jobject, jlong);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    firstPortAvailable
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_eepromprogrammer_SerialPortInterface_firstPortAvailable
  (JNIEnv *, jobject);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    writeToPort
 * Signature: (J[B)I
 */
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_writeToPort
  (JNIEnv *, jobject, jlong, jbyteArray);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    setBaudRate
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setBaudRate
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    setStopBits
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setStopBits
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    setByteSize
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setByteSize
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    readPort
 * Signature: (JI)[B
 */
JNIEXPORT jbyteArray JNICALL Java_eepromprogrammer_SerialPortInterface_readPort
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    nativeSetParity
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_nativeSetParity
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    setParityOn
 * Signature: (JZ)I
 */
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setParityOn
  (JNIEnv *, jobject, jlong, jboolean);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    setOutXCTSDSR
 * Signature: (JZ)I
 */
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setOutXCTSDSR
  (JNIEnv *, jobject, jlong, jboolean);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    setDTRControl
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setDTRControl
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     eepromprogrammer_SerialPortInterface
 * Method:    setDiscardNull
 * Signature: (JZ)I
 */
JNIEXPORT jint JNICALL Java_eepromprogrammer_SerialPortInterface_setDiscardNull
  (JNIEnv *, jobject, jlong, jboolean);

#ifdef __cplusplus
}
#endif
#endif
