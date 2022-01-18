@ECHO OFF

SETLOCAL
SET "JAVA_EXE=java"
:: Attempt to use JAVA_HOME
IF DEFINED JAVA_HOME (
  SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)
IF NOT EXIST "%JAVA_EXE%" (
  SET "JAVA_EXE=java"
)

IF EXIST "package.json" (
  SET "PREFIX=node_modules"
  SET "BINDIR=node_modules\.bin"
) ELSE (
  SET "PREFIX="
  SET "BINDIR=bin"
)

IF NOT EXIST "%BINDIR%\es4x-launcher.jar" (
  %JAVA_EXE% %JAVA_OPTS% -Dsilent-install -jar %~dp0\..\es4x-pm-${project.version}.jar %*
  IF %ERRORLEVEL% NEQ 0 (
    EXIT /B %ERRORLEVEL%
  )
)

:: Use JVMCI if installed
IF EXIST "%PREFIX%\.jvmci" (
  SET "JVMCI=--module-path=%PREFIX%\.jvmci -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --upgrade-module-path=%PREFIX%\.jvmci\compiler.jar"
)

:: If exists security.policy start the VM in secure mode
IF EXIST "security.policy" (
  SET "SECURITY_MANAGER=-Djava.security.manager -Djava.security.policy=security.policy"
)

:: If exists logging.properties start JUL using it
IF EXIST "logging.properties" (
  SET "LOGGING_PROPERTIES=-Djava.util.logging.config.file=logging.properties"
)

IF EXIST "%BINDIR%\es4x-launcher.jar" (
  %JAVA_EXE% -XX:+IgnoreUnrecognizedVMOptions %JVMCI% %SECURITY_MANAGER% %LOGGING_PROPERTIES% %JAVA_OPTS% -cp "%BINDIR%\es4x-launcher.jar;%~dp0\..\es4x-pm-${project.version}.jar" io.reactiverse.es4x.ES4X %*
) ELSE (
  ECHO "Please run: es4x install"
  EXIT /B 0
)
