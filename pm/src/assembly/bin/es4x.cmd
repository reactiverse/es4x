@ECHO OFF

SETLOCAL
:: Attempt to use JAVA_HOME
SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
IF NOT EXIST "%JAVA_EXE%" (
  SET "JAVA_EXE=java"
)

:: Use JVMCI if installed
IF EXIST "node_modules/.jvmci" (
  SET "JVMCI=--module-path=node_modules\.jvmci -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --upgrade-module-path=node_modules\.jvmci\compiler.jar"
)

:: If exists node_modules/.bin/es4x-launcher.jar
:: use it's class path (else rely on default runtime)
SET "APP_RUNTIME=node_modules\.bin\es4x-launcher.jar"
IF NOT EXIST "%APP_RUNTIME%" (
  SET "APP_RUNTIME=%~dp0\..\runtime\*"
)

"$JAVA_EXE" -XX:+IgnoreUnrecognizedVMOptions $JVMCI $JAVA_OPTS -cp "%APP_RUNTIME%;%~dp0\..\es4x-pm-${project.version}.jar" io.reactiverse.es4x.ES4X %*
