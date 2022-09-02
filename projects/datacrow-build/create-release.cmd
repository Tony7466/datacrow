@echo off
echo Setting JAVA_HOME
set JAVA_HOME=D:\Development\tools\Java\JDK
echo setting PATH
set PATH=D:\Development\tools\Java\JDK\bin;%PATH%
echo Display java version
echo Creation release %1
java -version
pause
REM ..
REM ## Build the individual packages
cd ..\datacrow-client\help\
call create_index.bat
cd ..
cd ..\datacrow-core
call build.bat
pause
cd ..\datacrow-services
call build.bat
pause
cd ..\datacrow-beans
call copy.bat
cd ..\datacrow-server
call build.bat
pause
cd ..\datacrow-client
call build.bat
pause
cd ..\datacrow-build
REM ..
REM ## Create the staging area
rd .\staging /S /Q
md .\staging\dc-client
md .\staging\dc-server
md .\staging\dc-client\services
md .\staging\dc-client\lib
md .\staging\dc-client\fonts
md .\staging\dc-client\plugins
md .\staging\dc-client\plugins\discid
md .\staging\dc-server\lib
md .\staging\dc-server\plugins
md .\staging\dc-server\webapp
md .\staging\dc-server\modules
REM ..
REM ## Copy the files from the various packages
move ..\datacrow-server\datacrow-server.jar staging\dc-server\
copy ..\datacrow-client\datacrow.jar staging\dc-client\
xcopy ..\datacrow-client\plugins\discid staging\dc-client\plugins\discid\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-core\lib staging\dc-client\lib\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-core\lib staging\dc-server\lib\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-client\lib staging\dc-client\lib\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-client\upgrade staging\dc-client\upgrade\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-client\fonts staging\dc-client\fonts\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-server\upgrade staging\dc-client\upgrade\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-client\readme staging\dc-client\readme\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-client\readme staging\dc-server\readme\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-server\lib staging\dc-client\lib\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-server\lib staging\dc-server\lib\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-services\lib staging\dc-client\lib\ /S /Y /EXCLUDE:release.exclude
copy ..\datacrow-plugins\_build\plugins\*.class .\staging\dc-client\plugins\ /Y
copy ..\datacrow-plugins\_build\plugins\*.class .\staging\dc-nw-client\plugins\ /Y
xcopy ..\datacrow-client\help staging\dc-client\help\ /S /EXCLUDE:release.exclude
xcopy ..\datacrow-client\icons staging\dc-client\icons\ /EXCLUDE:release.exclude
xcopy ..\datacrow-server\icons staging\dc-server\icons\ /EXCLUDE:release.exclude
xcopy ..\datacrow-server\webapp staging\dc-server\webapp\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-client\modules staging\dc-client\modules\ /S /EXCLUDE:release.exclude
xcopy ..\datacrow-client\modules staging\dc-server\modules\ /S /EXCLUDE:release.exclude
xcopy ..\datacrow-client\plugins\discid staging\dc-client\plugins\discid\ /S /Y /EXCLUDE:release.exclude
xcopy ..\datacrow-client\reports staging\dc-client\reports\ /S /EXCLUDE:release.exclude
xcopy ..\datacrow-client\resources staging\dc-client\resources\ /S /EXCLUDE:release.exclude
xcopy ..\datacrow-client\resources staging\dc-server\resources\ /S /EXCLUDE:release.exclude
xcopy ..\datacrow-client\services staging\dc-client\services\ /S /EXCLUDE:release.exclude
copy ..\datacrow-client\datacrow32bit.* staging\dc-client\
copy ..\datacrow-client\datacrow64bit.* staging\dc-client\
copy ..\datacrow-client\log4j.properties staging\dc-client\
copy ..\datacrow-server\log4j.properties staging\dc-server\
copy ..\datacrow-server\datacrow-server32bit.* staging\dc-server\
copy ..\datacrow-server\datacrow-server64bit.* staging\dc-server\
REM ..
REM ## Change the file properties within the staging area
chmod -R +r+w ./staging/*
cd ..\datacrow-install
call create-installer.bat
7z a -tZip datacrow_%1_installer.zip installer.jar installer.sh readme.txt
7z a -tZip datacrow_%1_windows_installer.zip installer.jar installer.sh readme.txt setup32bit.exe setup64bit.exe
move datacrow_%1_installer.zip ..\datacrow-build\staging\
move datacrow_%1_windows_installer.zip ..\datacrow-build\staging\
cd ..\datacrow-build\staging
7z a -tZip datacrow_%1_client_zipped.zip .\dc-client
7z a -tZip datacrow_%1_server_zipped.zip .\dc-server
cd ..
cd ..
7z a -tZip datacrow_%1_source.zip .\datacrow-client .\datacrow-core .\datacrow-server
move datacrow_%1_source.zip .\datacrow-build\staging\
cd .\datacrow-build\staging\
pause