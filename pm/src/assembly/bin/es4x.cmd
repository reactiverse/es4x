@ECHO OFF

SETLOCAL

SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
IF NOT EXIST "%JAVA_EXE%" (
  SET "JAVA_EXE=java"
)

"%JAVA_EXE%" -jar "%~dp0\es4x-bin.jar" %*
