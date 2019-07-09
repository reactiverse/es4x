@ECHO OFF

SETLOCAL
:: Attempt to use GRAALVM_HOME
SET "JAVA_EXE=%GRAALVM_HOME%\bin\java.exe"
IF NOT EXIST "%JAVA_EXE%" (
  :: Attempt to use JAVA_HOME
  SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)
IF NOT EXIST "%JAVA_EXE%" (
  SET "JAVA_EXE=java"
)

"%JAVA_EXE%" -jar "%~dp0\es4x-bin.jar" %*
