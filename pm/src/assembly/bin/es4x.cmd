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

:: Use JVMCI if installed
IF EXIST "node_modules/.jvmci" (
  SET "JVMCI=--module-path=node_modules\.jvmci -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --upgrade-module-path=node_modules\.jvmci\compiler.jar"
)

:: If exists node_modules/.bin/es4x-launcher.jar
:: use it's class path (else rely on default runtime)
IF EXIST "node_modules\.bin\es4x-launcher.jar" (
  IF EXIST "node_modules\.lib" (
    SET "APP_RUNTIME=node_modules\.bin\es4x-launcher.jar;%~dp0\..\es4x-pm-${project.version}.jar"
    SET "APP_MAIN=io.reactiverse.es4x.ES4X"
  ) ELSE (
    SET "APP_RUNTIME=%~dp0\..\es4x-pm-${project.version}.jar"
    SET "APP_MAIN=io.reactiverse.es4x.cli.PM"
  )
) ELSE (
  SET "APP_RUNTIME=%~dp0\..\es4x-pm-${project.version}.jar"
  SET "APP_MAIN=io.reactiverse.es4x.cli.PM"
)

:: If exists security.policy
:: start the VM in secure mode
IF EXIST "security.policy" (
  SET "SECURITY_MANAGER=-Djava.security.manager -Djava.security.policy=security.policy"
)

:: If exists logging.properties
:: start JUL using it
IF EXIST "logging.properties" (
  SET "LOGGING_PROPERTIES=-Djava.util.logging.config.file=logging.properties"
)

"$JAVA_EXE" -XX:+IgnoreUnrecognizedVMOptions %JVMCI% %SECURITY_MANAGER% %LOGGING_PROPERTIES% %JAVA_OPTS% -cp "%APP_RUNTIME%" %APP_MAIN% %*
