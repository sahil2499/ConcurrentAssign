@echo off

set java_file=Vacuum.java
set robot_file=robotsFiles.txt
set room_file=roomFile.txt

REM Compile the Java file
javac %java_file%

REM Check if the compilation was successful
if %errorlevel%==0 (
    echo Java compilation successful.
    
    REM Run the Java program with inputs from the files
    java Vacuum %robot_file% %room_file%
    
    REM Alternatively, if the Java program requires command-line arguments
    REM java vacuum arg1 arg2 %robot_file% %room_file%
) else (
    echo Java compilation failed. Please check the code and try again.
)
