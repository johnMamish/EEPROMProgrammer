::the path to vcvarsall.bat should be in your system variables.
::echo DLLCompile.bat <vcvarsall option>
@echo off
cls
IF "%1"=="help" GOTO :HELP
CALL vcvarsall.bat %1
cl -I DLL -I "C:\Program Files\Java\jdk1.7.0_02\include" -I "C:\Program Files\Java\jdk1.7.0_02\include\win32" -LD DLL\eepromprogrammer_SerialPortInterface.c
del eepromprogrammer_SerialPortInterface.exp eepromprogrammer_SerialPortInterface.obj eepromprogrammer_SerialPortInterface.lib
move eepromprogrammer_SerialPortInterface.dll DLL
goto DONE
:HELP
echo Make sure the path containing vcvarsall.bat is in your PATH system variable.
CALL vcvarsall.bat --help
GOTO DONE
:DONE
