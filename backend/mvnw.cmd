@REM Maven Wrapper script for Windows

@REM Set local scope for the variables with windows NT shell
@setlocal

@REM Find Java
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVACMD=java.exe
%JAVACMD% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found
echo Please set the JAVA_HOME variable in your environment
echo.
goto error

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVACMD=%JAVA_HOME%\bin\java.exe

:execute
@REM Execute Maven
%JAVACMD% %MAVEN_OPTS% -classpath .mvn\wrapper\maven-wrapper.jar "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*

:error
@endlocal
