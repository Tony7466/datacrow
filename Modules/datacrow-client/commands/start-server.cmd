@echo off
echo Setting JAVA_HOME
set JAVA_HOME=C:\Users\RJ\Documents\Development\tools\Java\JDK
echo setting PATH
set PATH=C:\Users\RJ\Documents\Development\tools\Java\JDK\bin;%PATH%
echo Display java version
echo Creation release %VERSION%
java -version
java -jar ./staging/dc-server/datacrow-server.jar -credentials:rwaals/nvn126611 -userdir:C:\Users\RJ\Data\Development\dc data -debug
pause