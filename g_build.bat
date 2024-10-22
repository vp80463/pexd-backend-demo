@echo off 
echo ==== 01. Show current gradle version of building ===
call gradlew.bat -v 
echo.

echo ==== 02. Compile and package  (auto-update the dependencies) ===
call gradlew.bat clean build -xtest --refresh-dependencies %*
echo.

echo ==== 03. Done. ===
echo --------------------------------
echo Press any key to exit....... 
pause>NUL 

@echo on